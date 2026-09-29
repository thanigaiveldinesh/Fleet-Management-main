package com.example.fleet;

import com.example.fleet.model.Vehicle;
import com.example.fleet.repository.InMemoryVehicleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping
public class McpController {

    private final FleetManager fleetManager =
            new FleetManager(new InMemoryVehicleRepository());
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // ── SSE endpoint ─────────────────────────────────────────────────────────
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse() {
        String sessionId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(sessionId, emitter);

        emitter.onCompletion(() -> emitters.remove(sessionId));
        emitter.onTimeout(() -> emitters.remove(sessionId));

        try {
            emitter.send(SseEmitter.event()
                    .name("endpoint")
                    .data("/message?sessionId=" + sessionId));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    // ── Message endpoint ──────────────────────────────────────────────────────
    @PostMapping("/message")
    public ResponseEntity<ObjectNode> message(
            @RequestParam String sessionId,
            @RequestBody JsonNode request) {

        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");

        JsonNode idNode = request.get("id");
        if (idNode != null) response.set("id", idNode);

        String method = request.path("method").asText();

        try {
            switch (method) {
                case "initialize" -> {
                    ObjectNode result = mapper.createObjectNode();
                    result.put("protocolVersion", "2024-11-05");
                    ObjectNode info = mapper.createObjectNode();
                    info.put("name", "fleet-management");
                    info.put("version", "1.0.0");
                    result.set("serverInfo", info);
                    ObjectNode caps = mapper.createObjectNode();
                    caps.set("tools", mapper.createObjectNode());
                    result.set("capabilities", caps);
                    response.set("result", result);
                }
                case "tools/list" -> {
                    ObjectNode result = mapper.createObjectNode();
                    ArrayNode tools = mapper.createArrayNode();
                    tools.add(makeTool("list_vehicles", "Return all vehicles in the fleet",
                            makeEmptySchema()));
                    tools.add(makeTool("get_vehicle", "Get a vehicle by ID",
                            makeSchema(Map.of("id", "number"))));
                    tools.add(makeTool("add_vehicle", "Add new vehicle as JSON string",
                            makeSchema(Map.of("vehicleJson", "string"))));
                    tools.add(makeTool("delete_vehicle", "Delete a vehicle by ID",
                            makeSchema(Map.of("id", "number"))));
                    tools.add(makeTool("describe_vehicle", "Describe a vehicle by ID",
                            makeSchema(Map.of("id", "number"))));
                    tools.add(makeTool("filter_vehicles", "Filter vehicles",
                            makeSchema(Map.of("filterType", "string", "filterValue", "string"))));
                    result.set("tools", tools);
                    response.set("result", result);
                }
                case "tools/call" -> {
                    String toolName = request.path("params").path("name").asText();
                    JsonNode args = request.path("params").path("arguments");
                    String text = callTool(toolName, args);
                    ObjectNode result = mapper.createObjectNode();
                    ArrayNode content = mapper.createArrayNode();
                    ObjectNode item = mapper.createObjectNode();
                    item.put("type", "text");
                    item.put("text", text);
                    content.add(item);
                    result.set("content", content);
                    response.set("result", result);
                }
                default -> {
                    response.set("result", mapper.createObjectNode());
                }
            }
        } catch (Exception e) {
            ObjectNode error = mapper.createObjectNode();
            error.put("code", -32603);
            error.put("message", e.getMessage());
            response.set("error", error);
        }

        // Send SSE notification
        SseEmitter emitter = emitters.get(sessionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(mapper.writeValueAsString(response)));
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok(response);
    }

    // ── Tool execution ────────────────────────────────────────────────────────
    private String callTool(String name, JsonNode args) throws Exception {
        return switch (name) {
            case "list_vehicles" ->
                    mapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(fleetManager.getAllVehicles());
            case "get_vehicle" -> {
                Long id = args.path("id").asLong();
                yield fleetManager.getVehicle(id)
                        .map(v -> { try { return mapper.writeValueAsString(v); }
                        catch (Exception e) { return e.getMessage(); }})
                        .orElse("Vehicle not found: " + id);
            }
            case "add_vehicle" -> {
                String json = args.path("vehicleJson").asText();
                Vehicle v = mapper.readValue(json, Vehicle.class);
                yield "Vehicle created with id: " + fleetManager.addVehicle(v).getId();
            }
            case "delete_vehicle" -> {
                Long id = args.path("id").asLong();
                fleetManager.deleteVehicle(id);
                yield "Vehicle " + id + " deleted.";
            }
            case "describe_vehicle" -> {
                Long id = args.path("id").asLong();
                yield fleetManager.getVehicle(id)
                        .map(v -> v.getType() + " - " + v.getMake() + " "
                                + v.getModel() + " (" + v.getYear() + ")")
                        .orElse("Vehicle not found: " + id);
            }
            case "filter_vehicles" -> {
                String filterType = args.path("filterType").asText();
                String filterValue = args.path("filterValue").asText();
                List<Vehicle> result = switch (filterType) {
                    case "fuelType" -> fleetManager.findByFuelType(filterValue);
                    case "numberOfWheels" -> fleetManager.findByNumberOfWheels(Integer.parseInt(filterValue));
                    case "ownerId" -> fleetManager.findByOwnerId(Long.parseLong(filterValue));
                    case "securityMeasure" -> fleetManager.findBySecurityMeasure(filterValue);
                    default -> fleetManager.getAllVehicles();
                };
                yield mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            }
            default -> "Unknown tool: " + name;
        };
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private ObjectNode makeTool(String name, String description, ObjectNode inputSchema) {
        ObjectNode tool = mapper.createObjectNode();
        tool.put("name", name);
        tool.put("description", description);
        tool.set("inputSchema", inputSchema);
        return tool;
    }

    private ObjectNode makeEmptySchema() {
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", "object");
        schema.set("properties", mapper.createObjectNode());
        return schema;
    }

    private ObjectNode makeSchema(Map<String, String> params) {
        ObjectNode schema = mapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode props = mapper.createObjectNode();
        params.forEach((key, type) -> {
            ObjectNode prop = mapper.createObjectNode();
            prop.put("type", type.equals("number") ? "integer" : type);
            props.set(key, prop);
        });
        schema.set("properties", props);
        ArrayNode required = mapper.createArrayNode();
        params.keySet().forEach(required::add);
        schema.set("required", required);
        return schema;
    }
}
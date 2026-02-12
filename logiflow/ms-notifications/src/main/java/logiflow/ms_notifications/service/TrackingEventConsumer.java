package logiflow.ms_notifications.service;

import logiflow.ms_notifications.client.PedidoServiceClient;
import logiflow.ms_notifications.config.RabbitMQConfig;
import logiflow.ms_notifications.dto.PedidoResponseDto;
import logiflow.ms_notifications.model.Notification;
import logiflow.ms_notifications.repository.NotificationRepository;
import logiflow.ms_notifications.utils.IdempotencyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrackingEventConsumer {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final PedidoServiceClient pedidoServiceClient;
    private final IdempotencyManager idempotencyManager;

    /**
     * Consumidor de eventos de ubicación del tracking service
     * Consume mensajes de la cola tracking.ubicacion
     */
    @RabbitListener(queues = RabbitMQConfig.TRACKING_QUEUE)
    public void handleTrackingLocationEvent(Map<String, Object> event) {
        log.info("📍 Evento de ubicación recibido: {}", event);

        try {
            // Extraer datos del evento
            String messageId = (String) event.get("messageId");
            String pedidoId = (String) event.get("pedidoId");
            Long repartidorId = ((Number) event.get("repartidorId")).longValue();
            Double latitud = ((Number) event.get("latitud")).doubleValue();
            Double longitud = ((Number) event.get("longitud")).doubleValue();
            String timestamp = (String) event.get("timestamp");

            log.info("📍 Procesando ubicación: PedidoID={}, RepartidorID={}, Lat={}, Lon={}, Timestamp={}",
                    pedidoId, repartidorId, latitud, longitud, timestamp);

            // Verificar idempotencia
            // CORRECTO: Nombre exacto y un solo parámetro String
if (messageId != null && idempotencyManager.isMessageProcessed(messageId)){
                log.warn("⏭️ Evento de ubicación ya procesado: {}", messageId);
                return;
            }

            // Obtener datos reales del pedido
            Optional<PedidoResponseDto> pedidoOpt = pedidoServiceClient.obtenerPedido(pedidoId);
            
            if (pedidoOpt.isEmpty()) {
                log.warn("⚠️ No se encontró información del pedido: {}", pedidoId);
                // Crear notificación genérica sin datos del pedido
                crearNotificacionUbicacion(pedidoId, null, repartidorId, latitud, longitud, timestamp);
            } else {
                PedidoResponseDto pedido = pedidoOpt.get();
                log.info("✅ Datos del pedido obtenidos: Cliente={}", 
                        pedido.getCliente() != null ? pedido.getCliente().getNombre() : "Desconocido");
                crearNotificacionUbicacion(pedidoId, pedido, repartidorId, latitud, longitud, timestamp);
            }

            // Marcar como procesado para idempotencia
            if (messageId != null) {
                idempotencyManager.markAsProcessed(messageId, "TRACKING_LOCATION");
            }

        } catch (Exception e) {
            log.error("❌ Error al procesar evento de ubicación: {}", event, e);
        }
    }

    /**
     * Crea y envía notificación de actualización de ubicación
     */
    private void crearNotificacionUbicacion(String pedidoId, PedidoResponseDto pedido, 
                                           Long repartidorId, Double latitud, Double longitud, String timestamp) {
        try {
            String clienteEmail;
            String nombreCliente;
            String asunto;
            String contenido;

            if (pedido != null && pedido.getCliente() != null) {
                // Usar datos reales del cliente
                PedidoResponseDto.ClienteDto cliente = pedido.getCliente();
                clienteEmail = cliente.getEmail() != null ? cliente.getEmail() : "cliente@logiflow.com";
                nombreCliente = cliente.getNombre() != null ? cliente.getNombre() : "Cliente";
                
                asunto = String.format("📍 Actualización de Ubicación - Pedido %s", pedidoId);
                contenido = String.format(
                        "Estimado/a %s,\n\n" +
                        "Tu pedido está en camino. Aquí está la ubicación actual del repartidor:\n\n" +
                        "📦 Información del Pedido:\n" +
                        "  - ID Pedido: %s\n" +
                        "  - Estado: %s\n" +
                        "  - Cobertura: %s\n\n" +
                        "📍 Ubicación del Repartidor #%d:\n" +
                        "  - Latitud: %.6f\n" +
                        "  - Longitud: %.6f\n" +
                        "  - Actualizado: %s\n\n" +
                        "Gracias por tu paciencia.\n\n" +
                        "Saludos,\n" +
                        "Equipo LogiFlow",
                        nombreCliente,
                        pedidoId,
                        pedido.getEstado() != null ? pedido.getEstado() : "EN_RUTA",
                        pedido.getCobertura() != null ? pedido.getCobertura() : "No especificado",
                        repartidorId,
                        latitud,
                        longitud,
                        timestamp
                );
            } else {
                // Datos genéricos cuando no se obtiene información del pedido
                clienteEmail = "notifications@logiflow.com";
                nombreCliente = "Cliente";
                
                asunto = String.format("📍 Actualización de Ubicación - Pedido %s", pedidoId);
                contenido = String.format(
                        "Estimado/a %s,\n\n" +
                        "Tu pedido está en camino.\n\n" +
                        "📦 Información del Pedido:\n" +
                        "  - ID Pedido: %s\n\n" +
                        "📍 Ubicación del Repartidor #%d:\n" +
                        "  - Latitud: %.6f\n" +
                        "  - Longitud: %.6f\n" +
                        "  - Actualizado: %s\n\n" +
                        "Gracias por tu paciencia.\n\n" +
                        "Saludos,\n" +
                        "Equipo LogiFlow",
                        nombreCliente,
                        pedidoId,
                        repartidorId,
                        latitud,
                        longitud,
                        timestamp
                );
            }

            // Crear y enviar notificación
            notificationService.createAndSendNotification(
                    pedidoId,
                    clienteEmail,
                    asunto,
                    contenido,
                    "TRACKING_UBICACION"
            );

            log.info("✅ Notificación de ubicación enviada a: {}", clienteEmail);

        } catch (Exception e) {
            log.error("❌ Error al crear notificación de ubicación para pedido: {}", pedidoId, e);
        }
    }
}

package com.proyecto.SafeRoute.utils;

import freemarker.template.Template;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import jakarta.servlet.http.HttpServletResponse;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.LocalDate;

/**
 * Utilidad para generar archivos PDF a partir de plantillas FreeMarker.
 * 
 * Esta clase se encarga de:
 * <ul>
 * <li>Cargar una plantilla HTML definida en FreeMarker.</li>
 * <li>Procesar la plantilla con un modelo de datos.</li>
 * <li>Convertir el resultado HTML en un documento PDF.</li>
 * <li>Enviar el PDF como respuesta HTTP al cliente.</li>
 * </ul>
 */
@Component
public class PdfGenerator {

    private final FreeMarkerConfigurer configurer;

    /**
     * Constructor de la clase PdfGenerator.
     * 
     * @param configurer configurador de FreeMarker para acceder a las plantillas
     */
    public PdfGenerator(FreeMarkerConfigurer configurer) {
        this.configurer = Objects.requireNonNull(configurer, "FreeMarkerConfigurer no puede ser null");
    }

    /**
     * Genera un archivo PDF a partir de una plantilla FreeMarker y una lista de
     * datos.
     *
     * @param templateName nombre de la plantilla (sin extensión) que se usará para
     *                     generar el PDF
     * @param datos        lista de objetos que se inyectarán en la plantilla
     *                     (ejemplo: lista de ReporteDTO)
     * @param desde        fecha inicial del filtro (puede ser null)
     * @param hasta        fecha final del filtro (puede ser null)
     * @param response     objeto HttpServletResponse donde se escribirá el PDF
     *                     generado
     * @throws Exception si ocurre algún error durante la carga de la plantilla o la
     *                   generación del PDF
     */
    public void generarPdf(String templateName, List<?> datos, LocalDate desde, LocalDate hasta,
                          HttpServletResponse response) throws Exception {
        
        Objects.requireNonNull(templateName, "templateName no puede ser null");
        Objects.requireNonNull(response, "response no puede ser null");
        
        // Crear modelo de datos para la plantilla
        Map<String, Object> model = new HashMap<>();
        model.put("reportes", datos);  // Cambiado de "clientes" a "reportes"
        model.put("desde", desde);
        model.put("hasta", hasta);
        model.put("fechaGeneracion", LocalDate.now());

        // Obtener y validar la plantilla (LÍNEA 74 CORREGIDA)
        Template template = configurer.getConfiguration().getTemplate(templateName + ".html", "UTF-8");
        Objects.requireNonNull(template, "Template '" + templateName + ".html' no encontrado");  // ← FIX línea 74
        
        // Procesar plantilla
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        // Configurar la respuesta HTTP
        response.setContentType("application/pdf");
        String filename = "reporte-" + templateName + "-" + LocalDate.now() + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        // Generar el PDF
        try (OutputStream out = response.getOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);
        }
    }
}

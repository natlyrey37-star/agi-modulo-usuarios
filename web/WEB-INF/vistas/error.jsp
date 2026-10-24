<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%-- Página de error de AGI: la directiva isErrorPage habilita el objeto implícito "exception" --%>
<%
    Object codigoEstado = request.getAttribute("jakarta.servlet.error.status_code");
    boolean noEncontrada = Integer.valueOf(404).equals(codigoEstado);
    String tituloPagina = noEncontrada ? "Página no encontrada" : "Ocurrió un error";
    if (exception != null) {
        // El detalle técnico va al registro del servidor, no a la pantalla del usuario
        application.log("Error en AGI", exception);
    }
%>
<%@ include file="/WEB-INF/vistas/fragmentos/encabezado.jspf" %>

<main class="login-container">
  <h1 class="titulo-pagina"><%= tituloPagina %></h1>
  <div class="mensaje error-message" role="alert">
    <% if (noEncontrada) { %>
      La página que busca no existe. Revise la dirección o vuelva al inicio.
    <% } else { %>
      No pudimos completar la operación. Verifique que la base de datos esté encendida e intente de nuevo.
    <% } %>
  </div>
  <a class="login-button" href="${pageContext.request.contextPath}/">Volver al inicio</a>
</main>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp" />
</body>
</html>

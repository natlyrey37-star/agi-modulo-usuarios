<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="co.edu.sena.agi.util.HtmlUtil" %>
<%-- Pantalla de inicio de sesión de AGI (página de bienvenida de la aplicación) --%>
<% String tituloPagina = "Iniciar sesión"; %>
<%@ include file="/WEB-INF/vistas/fragmentos/encabezado.jspf" %>

<main class="login-container">
  <h1 class="titulo-pagina">Iniciar sesión</h1>
  <p class="subtitulo">Bienvenido a AGI, su asistente para aprender a usar el celular.</p>

  <%@ include file="/WEB-INF/vistas/fragmentos/mensajes.jspf" %>

  <%-- Formulario con método POST: la contraseña no debe viajar en la URL --%>
  <form method="post" action="${pageContext.request.contextPath}/login">
    <div class="form-group">
      <label for="correo">Correo electrónico</label>
      <input class="form-control" type="email" id="correo" name="correo" required maxlength="100"
             autocomplete="email" value="<%= HtmlUtil.escapar(request.getAttribute("correoEscrito")) %>">
    </div>
    <div class="form-group">
      <label for="contrasena">Contraseña</label>
      <input class="form-control" type="password" id="contrasena" name="contrasena" required
             autocomplete="current-password">
    </div>
    <button class="login-button" type="submit">Ingresar</button>
  </form>

  <a class="link" href="${pageContext.request.contextPath}/usuarios?accion=nuevo">¿No tiene cuenta? Regístrese aquí</a>
</main>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp" />
</body>
</html>

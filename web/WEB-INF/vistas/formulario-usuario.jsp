<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, co.edu.sena.agi.util.HtmlUtil, co.edu.sena.agi.web.UsuarioServlet" %>
<%-- Formulario de registro (crear cuenta) y de edición de usuarios --%>

<%-- Acción useBean: toma el objeto "usuario" que dejó el servlet; si no existe, crea uno vacío --%>
<jsp:useBean id="usuario" class="co.edu.sena.agi.modelo.Usuario" scope="request" />
<%
    boolean esEdicion = "edicion".equals(request.getAttribute("modo"));
    String tituloPagina = esEdicion ? "Editar usuario" : "Crear cuenta";
    @SuppressWarnings("unchecked")
    List<String> errores = (List<String>) request.getAttribute("errores");
    boolean conSesion = session.getAttribute("usuarioSesion") != null;
    String rutaCancelar = (esEdicion || conSesion) ? "/usuarios" : "/index.jsp";
%>
<%@ include file="/WEB-INF/vistas/fragmentos/encabezado.jspf" %>

<main class="formulario-container">
  <h1 class="titulo-pagina"><%= tituloPagina %></h1>
  <p class="subtitulo">
    <%= esEdicion ? "Modifique los datos y guarde los cambios." : "Complete sus datos para registrarse en AGI." %>
  </p>

  <% if (errores != null && !errores.isEmpty()) { %>
  <div class="mensaje error-message" role="alert">
    Revise los siguientes datos:
    <ul>
      <% for (String error : errores) { %>
      <li><%= HtmlUtil.escapar(error) %></li>
      <% } %>
    </ul>
  </div>
  <% } %>

  <%-- Formulario con método POST: los datos van en el cuerpo de la petición --%>
  <form method="post" action="${pageContext.request.contextPath}/usuarios">
    <input type="hidden" name="accion" value="<%= esEdicion ? "actualizar" : "registrar" %>">
    <% if (esEdicion) { %>
    <input type="hidden" name="idUsuario" value="<jsp:getProperty name="usuario" property="idUsuario" />">
    <% } %>

    <div class="form-fila">
      <div class="form-group">
        <label for="nombres">Nombres</label>
        <input class="form-control" type="text" id="nombres" name="nombres" required maxlength="60"
               value="<%= HtmlUtil.escapar(usuario.getNombres()) %>">
      </div>
      <div class="form-group">
        <label for="apellidos">Apellidos</label>
        <input class="form-control" type="text" id="apellidos" name="apellidos" required maxlength="60"
               value="<%= HtmlUtil.escapar(usuario.getApellidos()) %>">
      </div>
    </div>

    <div class="form-fila">
      <div class="form-group">
        <label for="cedula">Cédula</label>
        <input class="form-control" type="text" id="cedula" name="cedula" required maxlength="15"
               inputmode="numeric" pattern="[0-9]{6,15}" title="Solo números, de 6 a 15 dígitos"
               value="<%= HtmlUtil.escapar(usuario.getCedula()) %>">
      </div>
      <div class="form-group">
        <label for="fechaNacimiento">Fecha de nacimiento</label>
        <input class="form-control" type="date" id="fechaNacimiento" name="fechaNacimiento" required
               max="<%= UsuarioServlet.obtenerFechaMaxima() %>"
               value="<%= HtmlUtil.escapar(usuario.getFechaNacimiento()) %>">
      </div>
    </div>

    <div class="form-group">
      <label for="correo">Correo electrónico</label>
      <input class="form-control" type="email" id="correo" name="correo" required maxlength="100"
             value="<%= HtmlUtil.escapar(usuario.getCorreo()) %>">
    </div>

    <div class="form-group">
      <label for="contrasena"><%= esEdicion ? "Nueva contraseña (opcional)" : "Contraseña" %></label>
      <input class="form-control" type="password" id="contrasena" name="contrasena" minlength="6"
             autocomplete="new-password" <%= esEdicion ? "" : "required" %>>
      <p class="form-ayuda">
        <%= esEdicion ? "Déjela en blanco para conservar la contraseña actual." : "Mínimo 6 caracteres." %>
      </p>
    </div>

    <div class="acciones-formulario">
      <button class="boton" type="submit"><%= esEdicion ? "Guardar cambios" : "Registrarme" %></button>
      <a class="boton boton-secundario" href="${pageContext.request.contextPath}<%= rutaCancelar %>">Cancelar</a>
    </div>
  </form>
</main>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp" />
</body>
</html>

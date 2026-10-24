<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, co.edu.sena.agi.modelo.Usuario, co.edu.sena.agi.util.HtmlUtil" %>
<%-- Listado y búsqueda de usuarios. Vista que muestra el UsuarioServlet. --%>
<%
    String tituloPagina = "Gestión de usuarios";
    @SuppressWarnings("unchecked")
    List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
    String busqueda = (String) request.getAttribute("busqueda");
%>
<%@ include file="/WEB-INF/vistas/fragmentos/encabezado.jspf" %>

<main class="main-container">
  <div class="card">
    <h1 class="titulo-pagina">Gestión de usuarios</h1>
    <p class="subtitulo">Personas registradas en AGI.</p>

    <%@ include file="/WEB-INF/vistas/fragmentos/mensajes.jspf" %>

    <div class="barra-herramientas">
      <%-- Formulario con método GET: el id viaja en la URL (?accion=buscar&id=...) --%>
      <form class="form-busqueda" method="get" action="${pageContext.request.contextPath}/usuarios">
        <input type="hidden" name="accion" value="buscar">
        <div>
          <label for="id">Buscar por id</label>
          <input class="form-control" type="number" id="id" name="id" min="1" required
                 value="<%= HtmlUtil.escapar(busqueda) %>">
        </div>
        <button class="boton" type="submit">Buscar</button>
        <% if (busqueda != null) { %>
        <a class="boton boton-secundario" href="${pageContext.request.contextPath}/usuarios">Ver todos</a>
        <% } %>
      </form>
      <a class="boton" href="${pageContext.request.contextPath}/usuarios?accion=nuevo">+ Nuevo usuario</a>
    </div>

    <table class="tabla-usuarios">
      <thead>
        <tr>
          <th>Id</th>
          <th>Nombre completo</th>
          <th>Cédula</th>
          <th>Correo</th>
          <th>Nacimiento</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
      <% if (usuarios == null || usuarios.isEmpty()) { %>
        <tr>
          <td class="tabla-vacia" colspan="6">No hay usuarios para mostrar.</td>
        </tr>
      <% } else {
             for (Usuario usuario : usuarios) { %>
        <tr>
          <td><%= usuario.getIdUsuario() %></td>
          <td><%= HtmlUtil.escapar(usuario.obtenerNombreCompleto()) %></td>
          <td><%= HtmlUtil.escapar(usuario.getCedula()) %></td>
          <td><%= HtmlUtil.escapar(usuario.getCorreo()) %></td>
          <td><%= usuario.getFechaNacimiento() %></td>
          <td>
            <div class="celda-acciones">
              <a class="boton boton-secundario boton-pequeno"
                 href="${pageContext.request.contextPath}/usuarios?accion=editar&amp;id=<%= usuario.getIdUsuario() %>">Editar</a>
              <%-- Eliminar usa POST porque modifica la base de datos --%>
              <form method="post" action="${pageContext.request.contextPath}/usuarios"
                    onsubmit="return confirm('¿Seguro que desea eliminar este usuario?');">
                <input type="hidden" name="accion" value="eliminar">
                <input type="hidden" name="idUsuario" value="<%= usuario.getIdUsuario() %>">
                <button class="boton boton-peligro boton-pequeno" type="submit">Eliminar</button>
              </form>
            </div>
          </td>
        </tr>
      <%     }
         } %>
      </tbody>
    </table>
    <p class="form-ayuda">Total: <%= usuarios == null ? 0 : usuarios.size() %> usuario(s).</p>
  </div>
</main>

<jsp:include page="/WEB-INF/vistas/fragmentos/pie.jsp" />
</body>
</html>

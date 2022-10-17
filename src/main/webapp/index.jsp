<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <meta
      name="viewport"
      content="width=device-width, initial-scale=1, shrink-to-fit=no"
    />
    <%@ page import="filesystem.ShowServlet" %>
<% ShowServlet.offset=0; %>
    <title>File Explorer</title>
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
      integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
      crossorigin="anonymous"
    />
  </head>

  <body>
    <div align="center">
      <h1>File Explorer</h1>

      <hr />
      <form action="FullSearchServlet" method="get">
        <input
          type="text"
          autocomplete="off"
          name="path"
          required
          placeholder="Enter path"
        />
        <input type="submit" value="Search" />
      </form>
      <hr />
    </div>
    <div class="card-deck center">
      <div class="card" style="width: 18rem">
        <h5 class="card-header">C:</h5>
        <div class="card-body">
          <form action="PathServlet" method="get">
            <a
              href="PathServlet?path=c:"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Load
            </a>
          </form>
         <br />
          <form action="SyncServlet" method="get">
            <a
              href="SyncServlet?path=c:"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Sync
            </a>
          </form>

          <%--
          <p class="card-text">Occupied Storage:</p>
          <p class="card-text">Directories:</p>
          <p class="card-text">Files:</p>
          --%>
          <form action="ShowServlet" method="get">
            <a
              style="float: right"
              href="ShowServlet?path=c:&temp=0"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Explore
            </a>
          </form>
        </div>
      </div>

      <div class="card" style="width: 18rem">
        <h5 class="card-header">D:</h5>
        <div class="card-body">
          <form action="PathServlet" method="get">
            <a
              href="PathServlet?path=d:"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Load
            </a>
          </form>
          <br />
          <form action="SyncServlet" method="get">
            <a
              href="SyncServlet?path=d:"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Sync
            </a>
          </form>

          <%--
          <p class="card-text">Occupied Storage:</p>
          <p class="card-text">Directories:</p>
          <p class="card-text">Files:</p>
          --%>
          <form action="ShowServlet" method="get">
            <a
              style="float: right"
              href="ShowServlet?path=d:&temp=0"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Explore
            </a>
          </form>
        </div>
      </div>
      <div class="card" style="width: 18rem">
        <h5 class="card-header">E:</h5>
        <div class="card-body">
          <form action="PathServlet" method="get">
            <a
              href="PathServlet?path=e:"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Load
            </a>
          </form>
          <br />
          <form action="SyncServlet" method="get">
            <a
              href="SyncServlet?path=e:"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Sync
            </a>
          </form>

          <%--
          <p class="card-text">Occupied Storage:</p>
          <p class="card-text">Directories:</p>
          <p class="card-text">Files:</p>
          --%>
          <form action="ShowServlet" method="get">
            <a
              style="float: right"
              href="ShowServlet?path=e:&temp=0"
              class="btn btn-outline-primary btn-md"
              name="path"
            >
              Explore
            </a>
          </form>
        </div>
      </div>
    </div>
  </body>
  <script
    src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
    integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
    crossorigin="anonymous"
  ></script>
  <script
    src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
    integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
    crossorigin="anonymous"
  ></script>
  <script
    src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
    integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
    crossorigin="anonymous"
  ></script>
</html>

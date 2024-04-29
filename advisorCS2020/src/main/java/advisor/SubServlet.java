package advisor;

import java.io.*;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import beans.*;
import advisor.*;

public class SubServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
	{
		String subPage=request.getParameter("page");
                String fwdPage = "/WEB-INF/students/" + subPage;
		RequestDispatcher rd = request.getRequestDispatcher(fwdPage);
		rd.forward(request,response);

   
	}
}


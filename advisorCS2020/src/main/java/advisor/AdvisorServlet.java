package advisor;

import java.io.*;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import beans.*;
import advisor.*;

public class AdvisorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
	{
			PrintWriter out = response.getWriter();
			
			// **** Get the Name-Value Pairs out of the Request Scope ****
			String lastName = request.getParameter("studentLastName");
			String firstName = request.getParameter("studentFirstName");
			String degreePlan = request.getParameter("degreePlan");
			String pastedTranscript = request.getParameter("academicTranscript");
			
			String webAppRoot = getServletContext().getRealPath("/");
			String degreePlanTitle = getDegreeTitle(degreePlan);

		String fwdPage="/WEB-INF/students/" + lastName +  firstName + degreePlan + "_check.html";
			Transcript t = new Transcript(pastedTranscript,degreePlan,lastName,firstName,webAppRoot+"/",degreePlanTitle);
			t.perform();

			//******** HTTP PAGE RESPONSE TO BROWSER **********

		RequestDispatcher rd = request.getRequestDispatcher(fwdPage);
		rd.forward(request,response);

   
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
	{
			doGet(request,response);		
	}	
	
	public String getDegreeTitle(String degreePlan){
		String prefix;
		String year;
		String title="";
		
		prefix = degreePlan.substring(0,3);
		year = degreePlan.substring(3,7);
		if(prefix.equals("dat")) title="BS in Information Technology: Data Analytics Concentration(C035)<br>Major Code: 1044";
		if(prefix.equals("prg")) title="BS in Information Technology: Programming Concentration(C033)<br>Major Code: 1044";
		if(prefix.equals("net")) title="BS in Information Technology: Networking Concentration(C034)<br>Major Code: 1044";
		if(prefix.equals("sec")) title="BS in Information Technology: Security Concentration(C037)<br>Major Code: 1044";		
		if(prefix.equals("gen")) title="BS in Information Technology: General(C039)<br>Major Code: 1044";	
		
		if(prefix.equals("prn")) title="BS in Information Technology: Programming Concentration(C033)<br>Networking Concentration(C034)<br>Major Code: 1044";
		if(prefix.equals("prs")) title="BS in Information Technology: Programming Concentration(C033)<br>Security Concentration(C037)<br>Major Code: 1044";
		if(prefix.equals("prd")) title="BS in Information Technology: Programming Concentration(C033)<br>Data Analytics Concentration(C035)<br>Major Code: 1044";
		if(prefix.equals("nwd")) title="BS in Information Technology: Networking Concentration(C034)<br>Data Analytics Concentration(C035)<br>Major Code: 1044";
		if(prefix.equals("nws")) title="BS in Information Technology: Networking Concentration(C034)<br>Security Concentration(C037)<br>Major Code: 1044";
		if(prefix.equals("das")) title="BS in Information Technology: Data Analytics Concentration(C035)<br>Security Concentration(C037)<br>Major Code: 1044";
		if(prefix.equals("csg")) title="BS in Computer Science: General Concentration(C039)<br>Major Code: 1044";
		if(prefix.equals("csd")) title="BS in Computer Science: Artificial Intelligence/Data Science Concentration(C051)<br>Major Code: 1044";
		if(prefix.equals("csc")) title="BS in Computer Science: CyberSecurity Concentration(C037)<br>Major Code: 1044";
		
			
		return title + "(" + year + ")";
	}
}


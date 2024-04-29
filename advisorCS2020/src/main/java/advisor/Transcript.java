package advisor;

import java.io.*;
import java.util.*;
import beans.*;

public class Transcript {
	private String pastedTranscript,DegreePlanFileName,SubFileName,DegreeCheckFileName,ITElectiveFileName;
	private String lastName, firstName, major="",GenEdFileName,ConcentrationFileName;
	public Vector<TranEntry> transcript;
	public Vector<DegPLEntry> degplan;
	public Vector<DegCheckEntry> degcheck;
	public Vector<SubEntry> substitute;
	public Vector<Student> studentList;
	
	public Transcript(String pastedTranscript, String DegreePlanFileName, String lastName, String firstName, String webAppRoot,String major){
 		this.firstName=firstName;
 		this.lastName=lastName;
		this.pastedTranscript = pastedTranscript;
 		this.DegreePlanFileName = webAppRoot + "WEB-INF/data/degreeplans/" + DegreePlanFileName + ".txt";
 		this.SubFileName = webAppRoot + "WEB-INF/data/substitutions/substitutions.txt";
 		this.GenEdFileName= webAppRoot + "WEB-INF/data/generaled/gened.txt"; 
 		this.ITElectiveFileName= webAppRoot + "WEB-INF/data/substitutions/itelective.txt"; 
 		this.ConcentrationFileName= webAppRoot + "WEB-INF/data/substitutions/concentration.txt"; 
 		this.DegreeCheckFileName= webAppRoot + "WEB-INF/students/" + lastName +  firstName + DegreePlanFileName + "_check.html";
  		this.major=major;
 		transcript = new Vector<TranEntry>();
 		degplan = new Vector<DegPLEntry>();
 		degcheck = new Vector<DegCheckEntry>();
 		substitute = new Vector<SubEntry>();
 		studentList = new Vector<Student>();
	}
	
	public void perform()throws IOException, FileNotFoundException  {
	
			LoadTranscript(pastedTranscript);
			LoadDegreePlan();	
			MatchCourses();
			//*** Apply Substitutions
			LoadSubstitutions(SubFileName);
			SubCourses();
			//*** Apply GenEd Courses
			LoadSubstitutions(GenEdFileName);
			SubCourses();
			//*** Apply Concentration Requirements
			LoadSubstitutions(ConcentrationFileName);
			SubCourses();	
			//*** Apply IT Elective Courses
			LoadSubstitutions(ITElectiveFileName);
			SubCourses();	
			DisplayDegreeCheck(lastName,firstName,major);
			DisplayRemainingCourses();
		}		
	
	public void LoadTranscript(String pastedTranscript) throws IOException, FileNotFoundException {
	/***********************************************************************
	 *  Method..........................................LoadTranscript     *
	 *---------------------------------------------------------------------*
	 * This method parses the student Academic Transcript, that has been   *
	 * copyed and pasted from LionsLink, and stores all of the courses     *
	 * taken by the student into the Vector transcript.  Each entry in the *
	 * vector contains the following stored in a TransEntry object:		   *
	 *																	   *
	 *		Course Prefix, Course No, Course Title,Grade,Semester Taken    *
	 ***********************************************************************/
		boolean flag1,flag2;
		String data,token1="",token2="",token3="",semesterCode=""; 
		StringTokenizer theTranscript = new StringTokenizer(pastedTranscript,"\n");
	   	while (theTranscript.hasMoreTokens())
	   	{
		   	data=theTranscript.nextToken();
		   	StringTokenizer s = new StringTokenizer(data,"\t");
		   	flag1=false;
		   	flag2=false;
		   	if(s.hasMoreTokens()){
			   	//**** First Token Course Prefix ******
				token1 = s.nextToken();
				if((token1.startsWith("Term:"))){
					String[] tokens = token1.split(" ");
					int index;
					semesterCode="";
					for(index=1;index<tokens.length;index++){
						semesterCode = semesterCode + tokens[index] + " ";
					}
				}	 
				char ch = token1.charAt(0);
				if(Character.isLetter(ch)) flag1=true;	
		   	}
		   	if(s.hasMoreTokens()){
			   	//**** Second Token Course Number ******
				token2 = s.nextToken(); 
				char ch = token2.charAt(0);
				if(Character.isDigit(ch)) flag2=true;	
		   	}			   	
		   	if(flag1&&flag2){
			   	TranEntry t = new TranEntry();
			   	t.coursePre = token1.trim();
			   	t.courseNo = token2.trim();
			   	if(s.hasMoreTokens()){
				   token3=s.nextToken().trim();
				   if(!isNumeric(token3)){
				      t.courseTitle=token3;			       //*** Course Title for Transfer Course
				   }else{   
				      t.courseTitle=s.nextToken().trim();  //**** Course Title for UAFS Course
			   	   }
			   	}
			   	if(s.hasMoreTokens()) t.grade=s.nextToken().trim();
			   	if(t.grade==null) t.grade="";
				//****** Get Credit Hours from Next Text Line ******
				theTranscript.nextToken().trim();
				t.creditHrs=theTranscript.nextToken().trim();
			   	t.semesterCode = semesterCode.trim();
			   	if((!t.grade.equals("F"))&&(!t.grade.equals("W"))&&(!t.grade.equals("WP"))&&
			   	   (!t.grade.equals("F*"))&&(!t.grade.equals("WC"))&&(!t.grade.equals("W*"))&&(!t.grade.equals("WP*"))&&(!t.grade.equals("D*"))){ 
			   		transcript.add(t);
					System.out.println(t.coursePre + "\t" + t.courseNo + "\t" + t.courseTitle + "\t" + t.grade + "\t" + t.creditHrs +  "\t" + t.semesterCode);			   		
		   		}
			  	
		   	}
	   		
			
		}	
	}
	

	public void LoadDegreePlan() throws IOException, FileNotFoundException {
	/***********************************************************************
	 *  Method..........................................LoadDegreePlan     *
	 *---------------------------------------------------------------------*
	 ***********************************************************************/
		String data;
		
		File inputFile = new File(DegreePlanFileName);
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
   
	   	while ((data = in.readLine()) != null)
	   	{
		   	StringTokenizer s = new StringTokenizer(data,"\t");
		   	if(s.hasMoreTokens()){
			   	DegPLEntry dp = new DegPLEntry();
			   	dp.dplanName = s.nextToken().trim();
			   	dp.semester = s.nextToken().trim();
			   	dp.coursePre = s.nextToken().trim();
			   	dp.courseNo = s.nextToken().trim();
			   	dp.courseTitle = s.nextToken().trim();
			   	degplan.add(dp);
			   	//System.out.println(dp.dplanName + "\t" + dp.semester + "\t" + dp.coursePre + " " + dp.courseNo + "\t" + dp.courseTitle);
		   	}
		}	
		in.close();		
	}	

	public static boolean isNumeric(String stringVal){
	//*****************************************************************
	//* Method.............................................isNumeric  *
	//*---------------------------------------------------------------*
	//* This method analyzes a string value to see it if              *
	//* consists of only digits 0-9.                                  *
	//*       Parameters:  (String)  stringVal (String to Be Analyzed)*
	//*       Return Value:(boolean) True-String is Digits only       *
	//*                              False-String contains non-digits *
	//*****************************************************************
	   boolean status=true;
	   int currentPos;
	   char currentChar;
	   int charCode;
	      
	   for(currentPos=0;currentPos<stringVal.length();currentPos++){
	      currentChar = stringVal.charAt(currentPos);
	      charCode = (int) currentChar;	      
	      if((charCode<48)||(charCode>57)){
		      status=false;
	      }
	   }
	   return status;
	}
	
	
	public void LoadSubstitutions(String fileName) throws IOException, FileNotFoundException {
	/***********************************************************************
	 *  Method..........................................LoadSubstitutions  *
	 *---------------------------------------------------------------------*
	 ***********************************************************************/
		String data;
		int header=3,lineCount=0;
		
		substitute = new Vector<SubEntry>();
		File inputFile = new File(fileName);
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
   
	   	while ((data = in.readLine()) != null)
	   	{
		   	lineCount++;
		   	if(lineCount>header){
			   	StringTokenizer s = new StringTokenizer(data,"\t");
			   	if(s.hasMoreTokens()){
				   	SubEntry sub = new SubEntry();
				   	sub.coursePre = s.nextToken().trim();
				   	sub.courseNo = s.nextToken().trim();
				   	sub.subCourses = s.nextToken().trim();
				   	substitute.add(sub);
			   	}
	   		}
		}	
		in.close();		
	}		
	
		
	public void MatchCourses(){
	/***********************************************************************
	 *  Method............................................MatchCourses     *
	 *---------------------------------------------------------------------*
	 ***********************************************************************/
		for(int i=0;i<degplan.size();i++){
			DegPLEntry courseReq = (DegPLEntry) degplan.elementAt(i);
			
			DegCheckEntry checkReq = new DegCheckEntry();
			checkReq.semester = courseReq.semester;
			checkReq.coursePre = courseReq.coursePre;
			checkReq.courseNo = courseReq.courseNo;
			checkReq.courseTitle = courseReq.courseTitle;
			checkReq.coursePreTaken = "";
			checkReq.courseNoTaken = "";
			checkReq.courseCHours = "";
			checkReq.courseGrade = "";
			checkReq.semesterTaken = "";
			checkReq.reqMet = "";	
			/*****Search Transcript *****/
			for(int j=0;j<transcript.size();j++){
				TranEntry course = (TranEntry) transcript.elementAt(j);			
				if(((courseReq.coursePre).equals(course.coursePre))&&
					((courseReq.courseNo).equals(course.courseNo))){
							
					checkReq.coursePreTaken = course.coursePre;
					checkReq.courseNoTaken = course.courseNo;
					checkReq.courseCHours = course.creditHrs;
					checkReq.courseGrade = course.grade;
					checkReq.semesterTaken = course.semesterCode;
					checkReq.reqMet = "Y";
					transcript.removeElementAt(j);
					j--;
				} 	
			}	
			degcheck.add(checkReq);
		}
 	}
 
	public void SubCourses(){
	/***********************************************************************
	 *  Method..............................................SubCourses     *
	 *---------------------------------------------------------------------*
	 ***********************************************************************/
	 
	    //*** Look For Course on Degree Plan where PreReq is not met***
		for(int i=0;i<degcheck.size();i++){
			DegCheckEntry DCEntry = (DegCheckEntry) degcheck.elementAt(i);
			if((DCEntry.reqMet.equals(""))){	 
				DCEntry=SearchForSubs(DCEntry);
				degcheck.set(i,DCEntry);
			} 
 		}	
 	}
 	
	public DegCheckEntry SearchForSubs(DegCheckEntry DCEntry){
	/***********************************************************************
	 *  Method...........................................SearchForSubs     *
	 *---------------------------------------------------------------------*
	 ***********************************************************************/
	 
	    //*** Look for Substitute Options for the Course***
		for(int i=0;i<substitute.size();i++){
			SubEntry sub = (SubEntry) substitute.elementAt(i);
			//**** Is this a Substitute Option for this Course
			if((DCEntry.coursePre.equals(sub.coursePre))&&
				(DCEntry.courseNo.equals(sub.courseNo))){
				//**** Scan Student Transcript for Substitute Courses
				DCEntry=ScanTranscript(DCEntry,sub);
				//**** If Substitute Found Return Modified Degree Plan Entry
				if((DCEntry.reqMet).equals("S")||(DCEntry.reqMet).equals("Y")){
					return DCEntry;	
				}		
			}			
		}
		return DCEntry;
	} 
 	
 	public DegCheckEntry ScanTranscript(DegCheckEntry DCEntry, SubEntry sub){
	/***********************************************************************
	 *  Method...........................................ScanTranscript    *
	 *---------------------------------------------------------------------*
	 ***********************************************************************/
	 	boolean validSub=true,courseFound=false;
	 	String coursePreHold="",courseNoHold="",gradeHold="",semesterHold="",titleHold="",cHoursHold="";
	 	Vector<Integer> tranids = new Vector<Integer>();
	 
	 	StringTokenizer courses = new StringTokenizer(sub.subCourses);
	 	while(courses.hasMoreTokens()){
			String subCoursePre = courses.nextToken();
			String subCourseNo = courses.nextToken();
			courseFound=false;
			for(int i=0;i<transcript.size();i++){
				TranEntry tranCourse = (TranEntry) transcript.elementAt(i);
				if(tranCourse!=null){
					//*** If Sub Course found on Transcript
					if(((tranCourse.coursePre).equals(subCoursePre))&&
						((tranCourse.courseNo).equals(subCourseNo))){
						courseFound=true;		
						coursePreHold = coursePreHold + tranCourse.coursePre + "<br>";
						courseNoHold = courseNoHold + tranCourse.courseNo + "<br>";
						cHoursHold = cHoursHold + tranCourse.creditHrs + "<br>";
						gradeHold = gradeHold + tranCourse.grade + "<br>";
						semesterHold = semesterHold + tranCourse.semesterCode + "<br>";
						titleHold = titleHold + tranCourse.courseTitle + "<br>"; 
						tranids.add(new Integer(i));
						i=transcript.size();
					}
				}	 				
			} 	
			if(courseFound==false) validSub=false;
	 	}
	 	if(validSub){
		 	//**** If a Valid Substitution Update Course Information
		 	//**** on the Degree Check Entry
			DCEntry.coursePreTaken = coursePreHold;
			DCEntry.courseNoTaken = courseNoHold;
			DCEntry.courseCHours = cHoursHold;
			DCEntry.courseGrade = gradeHold;
			DCEntry.semesterTaken = semesterHold;
			DCEntry.courseTitleTaken = titleHold;
			if((DCEntry.coursePre).equals("GNED")||(DCEntry.coursePre).equals("EEEE")){
				DCEntry.reqMet = "Y";
			}else{
				DCEntry.reqMet = "S";
			}
			for(int i=0;i<tranids.size();i++){
				Integer IntWrap = (Integer) tranids.elementAt(i);
				transcript.set(IntWrap.intValue(),null);
			}		 			 	
	 	}
	 	return DCEntry;
 	}
	 	
	 	
	public void DisplayRemainingCourses(){
		//******** Show Courses Left *******
		for(int i=0;i<transcript.size();i++){
			TranEntry e = (TranEntry) transcript.elementAt(i);
			if(e!=null){
			/*	System.out.println(e.coursePre + "\t" +
								e.courseNo + "\t" +
								e.courseTitle + "\t" +
								e.grade + "\t" +
								e.creditHrs + "\t" +
								e.semesterCode + "\t"); */
			}
		}	 
 	} 	
 	
 	
 
  
 	public void DisplayDegreeCheck(String lname,String fname, String major) throws IOException, FileNotFoundException{
	/***************************************************************
	 *  Method...............................DisplayDegreeCheck()  *
	 ***************************************************************/ 	
		
		File outFile = new File(DegreeCheckFileName);
		PrintWriter out = new PrintWriter(new FileWriter(outFile));
		Date date=new java.util.Date();  
   
		
		out.println("<!DOCTYPE html>");
		out.println("<head><title>Degree Audit</title>");
		out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
		out.println("<link href=\"styles/dcheck.css\" rel=\"stylesheet\" type=\"text/css\" /></head>\n<body>\n");
		out.println("<img src=\"uafs.png\" alt=\"Logo\">");
		out.println("<p class=\"info\"><b>" + lname + ", " + fname + "</b><br>College of Science, Technology, Engineering, and Mathematics<br>"
				+ major + "<br>Degree Audit (Generated: " + date + ") </p>");

		//********* Show Degree Plan *******
		String lastsemester="";
		for(int i=0;i<degcheck.size();i++){
			DegCheckEntry e = (DegCheckEntry) degcheck.elementAt(i);
			if(!(lastsemester.equals(e.semester))){
				if(!(lastsemester.equals(""))) out.println("</table>");
				lastsemester=e.semester;
				out.println("<h3>" + lastsemester + "</h3>");
				out.println("<table><tr><th>Course Pre</th><th>Course No.</th><th>Title</th>" +
							"<th>Pre Taken</th><th>No. Taken</th><th>Credit Hours</th><th>Grade</th><th>Semester</th>" +
							"<th>Req. Met</th></tr>");
			}
			out.println("<tr><td>" + e.coursePre + "</td><td>" +
							e.courseNo + "</td><td>" +
							e.courseTitle + "</td><td>" +
							e.coursePreTaken + "</td><td>" +
							e.courseNoTaken + "</td><td>" +
							e.courseCHours + "</td><td style=\"text-align: center;\">" +
							e.courseGrade + "</td><td>" +
							e.semesterTaken + "</td><td style=\"text-align: center;\">" +
							e.reqMet + "</td></tr>");
		} 	
		out.println("</table>");

		out.println("\n<h3>Unused Courses</h3><table>");
		
		//******** Show Remaining Courses *******
		for(int i=0;i<transcript.size();i++){
			TranEntry e = (TranEntry) transcript.elementAt(i);
			if(e!=null){
				out.println("<tr><td>" + e.coursePre + "</td><td>" +
							e.courseNo + "</td><td>" +
							e.courseTitle + "</td><td>" +
							e.creditHrs + "</td><td>" +
							e.grade + "</td><td>" +
							e.semesterCode + "</td></tr>");
			}
		}	 		
		out.println("</table>");
        out.println("</body></html>");
	 	out.close();
 	}	
		

}

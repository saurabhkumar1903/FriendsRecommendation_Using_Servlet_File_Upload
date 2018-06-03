package demopackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
//converting output from hadoop for input in Neo4j Platform
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.Request;

/**
 * Servlet implementation class Processing
 */
public class Processing extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Processing() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.getWriter().append("Output Generated: "+request.getAttribute("output_path"));
		//RequestDispatcher rd=new RequestDispatcher();
		//RequestDispatcher rd=request.getRequestDispatcher("http://localhost:7474/browser/");  
		//rd.forward(
			//	request, response);
		response.sendRedirect("http://localhost:7474/browser/");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String input_path = (String) request.getAttribute("output_path");
		String line = null;
		String temp = "";
		try

		{

			FileReader fileReader = new FileReader(input_path + "/part-r-00000");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				int lentab = line.indexOf("\t");
				//temp = line.substring(0, lentab) + "," + line.substring(lentab + 1, line.length());
					temp=line.substring(0,lentab);
					if((line.substring(lentab + 1, line.length())).length()>0)
						temp+="," + line.substring(lentab + 1, line.length());
					
				try {

					FileWriter fileWriter = new FileWriter(input_path + "/output.csv", true);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					bufferedWriter.write(temp);
					bufferedWriter.write("\n");
					bufferedWriter.close();
				} catch (IOException ex1) {
					System.out.println("Error writing to file '" + input_path+ "/part-r-00000" + "'");

				}

			}

			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + input_path+ "/part-r-00000" + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + input_path+ "/part-r-00000"+ "'");
		}
		doGet(request, response);
	}

}

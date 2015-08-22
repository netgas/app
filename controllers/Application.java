package controllers;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.data.DynamicForm;
import java.io.*;
import java.util.Scanner;
import java.lang.Thread;
//end
//SAS
//import java.sql.*;
//import java.util.*;
//import com.sas.iom.SAS.*;
//import com.sas.iom.WorkspaceFactory;
//import com.sas.rio.*;

import views.html.*;

public class Application extends Controller {

	public static String base_path = "D:\\data\\igbo\\scenarios2\\";
	public static String network_file_path = "D:\\data\\igbo\\network2.txt";

    public static Result network() throws FileNotFoundException {
    	response().setContentType("text/html");
        return ok(new java.io.File(network_file_path));
    }

    public static Result network2() throws FileNotFoundException {
        response().setContentType("text/html");
        return ok(new java.io.File(network_file_path));
    }


    public static Result load_scenario(String scenario) throws FileNotFoundException {
    	response().setContentType("text/html");
        return ok(new java.io.File(base_path + scenario + ".txt"));
    }

    public static Result load_scenario2(String scenario) throws FileNotFoundException {
        response().setContentType("text/html");
        return ok(new java.io.File(base_path + scenario + ".txt"));
    }

    public static Result load_optimized(String scenario) throws FileNotFoundException {
        response().setContentType("text/html");
        return ok(new java.io.File(base_path + scenario + ".ver"));
    }

    public static Result load_scenario_ver(String scenario) throws FileNotFoundException {
        response().setContentType("text/html");
        return ok(new java.io.File(base_path + scenario + ".ver"));
    }

    public static Result load_scenario_nodes(String scenario) throws FileNotFoundException {
    	response().setContentType("text/html");
        return ok(new java.io.File("F:\\data\\igbo\\" + scenario + ".nod"));
    }

    public static Result load_scenario_pipes(String scenario) throws FileNotFoundException {
    	response().setContentType("text/html");
        return ok(new java.io.File("F:\\data\\igbo\\results\\" + scenario + ".pip"));
    }

    public static Result netviewid() {
        return ok(netviewid.render("network2.txt"));
    }

    public static Result admin_field() {
        return ok(admin_field.render("network2.txt"));
    }

    public static Result admin_psc() {
        return ok(admin_psc.render("network2.txt"));
    }

    public static Result admin_ucgr() {
        return ok(admin_ucgr.render("network2.txt"));
    }

    public static Result admin() {
        return ok(admin.render("network2.txt"));
    }

    public static Result netviewids() {
        return ok(netviewids.render("network2.txt"));
    }

    public static Result netviewidrev() {
        return ok(netviewidrev.render("network2.txt"));
    }

    public static Result netviewidlong() {
        return ok(netviewidlong.render("network2.txt"));
    }


    //List Scenario Files
    public static Result list_scenarios() {
    	StringBuilder html = new StringBuilder();
    	html.append("<select id='filename' class='form-control'>");
    	File folder = new File("F:\\data\\igbo\\scenarios");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
                //System.out.println("File " + listOfFiles[i].getName());
                String filename = listOfFiles[i].getName();
                int filename_length = filename.length();
                if(filename_length>3) {
                    String extension = filename.substring(filename_length-3,filename_length);
                    //System.out.println(extension);
                    if(extension.equals("txt")) {
                        html.append("<option>");
                        html.append(listOfFiles[i].getName());
                        html.append("</option>");
                    }
                }
                

		        
		      } else if (listOfFiles[i].isDirectory()) {
		        System.out.println("Directory " + listOfFiles[i].getName());
		      }
		    }
		html.append("</select>");
		String finalHtml = html.toString();
		return ok(finalHtml);
    }

    public static Result list_scenarios2() {
        StringBuilder html = new StringBuilder();
        html.append("<select id='filename' class='form-control'>");
        File folder = new File(base_path);
        File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
              if (listOfFiles[i].isFile()) {
                //System.out.println("File " + listOfFiles[i].getName());
                String filename = listOfFiles[i].getName();
                int filename_length = filename.length();
                if(filename_length>3) {
                    String extension = filename.substring(filename_length-3,filename_length);
                    //System.out.println(extension);
                    if(extension.equals("txt")) {
                        html.append("<option>");
                        html.append(listOfFiles[i].getName());
                        html.append("</option>");
                    }
                }
                

                
              } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
              }
            }
        html.append("</select>");
        String finalHtml = html.toString();
        return ok(finalHtml);
    }

    //Save Scenario
    public static Result save_scenario() {
    	DynamicForm requestData = Form.form().bindFromRequest();
        String scenario_filename = requestData.get("scenario_filename");
        String scenario_data = requestData.get("scenario_data");
        System.out.print(scenario_filename);
        //String nominations_data = requestData.get("nominations_data");
        //Write file
        try {
            File newScenarioFile = new File(base_path + scenario_filename + ".txt");
            FileWriter fw = new FileWriter(newScenarioFile);
            fw.write(scenario_data);
            fw.close();
            //Nominations
            //File newNominationsFile = new File("F:\\gitjava\\netgas\\scenarios\\" + scenario_filename + ".nom");
            //FileWriter fw2 = new FileWriter(newNominationsFile);
            //fw2.write(nominations_data);
            //fw2.close();

        } catch (IOException iox) {
            //send Error
            return ok("1");
        }
        return ok("0");
    }

    public static Result save_scenario2() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String scenario_filename = requestData.get("scenario_filename");
        String scenario_data = requestData.get("scenario_data");
        System.out.print(scenario_filename);
        //String nominations_data = requestData.get("nominations_data");
        //Write file
        try {
            File newScenarioFile = new File(base_path + scenario_filename + ".txt");
            FileWriter fw = new FileWriter(newScenarioFile);
            fw.write(scenario_data);
            fw.close();
            //Nominations
            //File newNominationsFile = new File("F:\\gitjava\\netgas\\scenarios\\" + scenario_filename + ".nom");
            //FileWriter fw2 = new FileWriter(newNominationsFile);
            //fw2.write(nominations_data);
            //fw2.close();

        } catch (IOException iox) {
            //send Error
            return ok("1");
        }
        return ok("0");
    }
    public static Result dummy_save() {
    	DynamicForm requestData = Form.form().bindFromRequest();
        String scenario_filename = requestData.get("scenario_filename");
        String scenario_data = requestData.get("scenario_data");
    	return ok("filename:" + scenario_filename);
    }

    public static Result run_scenario(String scenario) {
        //Get File length
        File f = new File(base_path + scenario + ".txt");
        long filesize = f.length();
        try {
            Process p = null;
            ProcessBuilder pb = new ProcessBuilder("F:\\data\\igbo\\scenarios\\dua.exe", scenario + ".txt");
            pb.directory(new File("F:\\data\\igbo\\scenarios\\"));
            p = pb.start();
            int runs = 0;
            while(runs < 180) {
                Thread.sleep(500);
                long new_filesize = f.length();
                if(new_filesize>filesize || new_filesize<1000) return ok("0");
                runs++;
            }
        } catch(Exception e) {
            System.out.println(e.getClass().getName());
            System.out.println(e.getMessage());
            return ok("-1");
        }
        
        //return ok(String.valueOf(filesize));
        return ok("1");
    }

    public static Result run_scenario2(String scenario) {
        //Get File length
        File f = new File(base_path + scenario + ".txt");
        long filesize = f.length();
        try {
            Process p = null;
            ProcessBuilder pb = new ProcessBuilder(base_path + "dua.exe", scenario + ".txt");
            pb.directory(new File(base_path));
            p = pb.start();
            int runs = 0;
            while(runs < 180) {
                Thread.sleep(500);
                long new_filesize = f.length();
                if(new_filesize>filesize || new_filesize<1000) return ok("0");
                runs++;
            }
        } catch(Exception e) {
            System.out.println(e.getClass().getName());
            System.out.println(e.getMessage());
            return ok("-1");
        }
        
        //return ok(String.valueOf(filesize));
        return ok("1");
    }


    public static Result run_optimization(String scenario) {
        //Get File length
        File f = new File(base_path + scenario + ".txt");
        long filesize = f.length();
        try {
            Process p = null;
            ProcessBuilder pb = new ProcessBuilder(base_path + "optim.exe", scenario + ".txt");
            pb.directory(new File(base_path));
            p = pb.start();
            int runs = 0;
            while(runs < 180) {
                Thread.sleep(500);
                long new_filesize = f.length();
                if(new_filesize>filesize || new_filesize<1000) return ok("0");
                runs++;
            }
        } catch(Exception e) {
            System.out.println(e.getClass().getName());
            System.out.println(e.getMessage());
            return ok("-1");
        }
        
        //return ok(String.valueOf(filesize));
        return ok("1");
    }


    //Call SAS Server
    public static Result run_scenario_sas() {
        /*
        Properties iomServerProperties = new Properties();
        iomServerProperties.put("host","192.168.1.98");
        iomServerProperties.put("port","8591");
        iomServerProperties.put("userName","sassrv");
        iomServerProperties.put("password","UserUser1");
        Properties[] serverList = {iomServerProperties};
        // Connect to the Workspace
        WorkspaceFactory climf = new WorkspaceFactory();
        IWorkspace climWorkspace =
        climf.createWorkspaceByServer(iomServerProperties);
        ILanguageService climLang = climWorkspace.LanguageService();
        //Acquire the Stored process service
        IStoredProcessService climSP =climLang.StoredProcessService();
        //Set the repository of the Stored process
        climSP.Repository("file:/IGBO");
        //Set the program and supply the required Parameters
        //climSP.Execute("sample1"," prd1=xxx");
        try {
            climSP.Execute("Integra","");
        } catch (Exception e){
            System.out.println(e.getClass().getName());
            System.out.println(e.getMessage());
            return ok("error");
        }

        */
        //prd1 - can be the user supplied Parameters!
        //JDBC Connect using MVA
        //IDataService climDataService = climWorkspace.DataService();
        //java.sql.Connection climconnect = new MVAConnection(climDataService,
        //new Properties());
        //java.sql.Statement statement = climconnect.createStatement();
        //java.sql.ResultSet rs = statement.executeQuery("Select * from work.one");
        //while(rs.next()) {
        //    String region = rs.getString("Region");
        //    out.println(region);
        //}


        return ok("0");
    }

}

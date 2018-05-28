package com.trackingbus.controller;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;

import resources.ApplicationMailer;
import resources.Assets;
import resources.RandomNumber;
import resources.Result;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.trackingbus.bean.AdminSetupBean;
import com.trackingbus.bean.CategoryContactBean;
import com.trackingbus.bean.CityBean;
import com.trackingbus.bean.ContactContentBean;
import com.trackingbus.bean.CountryBean;
import com.trackingbus.bean.DriverBean;
import com.trackingbus.bean.DriverTrack;
import com.trackingbus.bean.EmailTemplateBean;
import com.trackingbus.bean.FaqBean;
import com.trackingbus.bean.FeaturesBean;
import com.trackingbus.bean.GoogleResponse;
import com.trackingbus.bean.HolidayBean;
import com.trackingbus.bean.HolidayDeletedBean;
import com.trackingbus.bean.LoginBean;
import com.trackingbus.bean.NationlityBean;
import com.trackingbus.bean.PageBean;
import com.trackingbus.bean.PageContentBean;
import com.trackingbus.bean.RouteBean;
import com.trackingbus.bean.RouteLatLng;
import com.trackingbus.bean.SchoolBean;
import com.trackingbus.bean.SchoolClasses;
import com.trackingbus.bean.SliderBean;
import com.trackingbus.bean.StudentBean;
import com.trackingbus.bean.SubscriberBean;
import com.trackingbus.bean.VechileBean;
import com.trackingbus.bean.relationshipBean;
import com.trackingbus.model.AdminSetupModel;
import com.trackingbus.model.CategoryContactModel;
import com.trackingbus.model.CityModel;
import com.trackingbus.model.ContactContentModel;
import com.trackingbus.model.CountryModel;
import com.trackingbus.model.DriverAttendanceModel;
import com.trackingbus.model.DriverDocModel;
import com.trackingbus.model.DriverModel;
import com.trackingbus.model.EmailTemplateModel;
import com.trackingbus.model.FaqModel;
import com.trackingbus.model.FeaturesModel;
import com.trackingbus.model.HolidayDeletedModel;
import com.trackingbus.model.HolidayModel;
import com.trackingbus.model.LoginModel;
import com.trackingbus.model.NationlityModel;
import com.trackingbus.model.PageContentModel;
import com.trackingbus.model.PageModel;
import com.trackingbus.model.RouteLatLngModel;
import com.trackingbus.model.RouteModel;
import com.trackingbus.model.SchoolClassesModel;
import com.trackingbus.model.SchoolModel;
import com.trackingbus.model.SliderModel;
import com.trackingbus.model.StudentModel;
import com.trackingbus.model.SubscriberModel;
import com.trackingbus.model.VehicleDocModel;
import com.trackingbus.model.VehicleModel;
import com.trackingbus.model.relationshipModel;
import com.trackingbus.service.FrontDashboardService;
import com.trackingbus.service.LoginService;
import com.trackingbus.service.SchoolService;
import com.trackingbus.service.StudentService;
import com.trackingbus.service.VehicleService;

import resources.Sms_api;

import java.util.*;
import java.util.concurrent.TimeUnit;
@Controller
public class SuperAdmin {

	@Autowired
	private SchoolService schoolservice;

	@Autowired
	private StudentService studentservice;

	@Autowired
	private LoginService loginservice;

	@Autowired
	private FrontDashboardService frontService;

	@Autowired
	private VehicleService vechileservice;
	
	@RequestMapping(value = "/adminDashboard", method = RequestMethod.GET)
	public ModelAndView adminDashboard(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin") {
  		     return new ModelAndView("redirect:/login.html") ;
		}
		int total_schools = schoolservice.listSchools().size();
		int total_students = schoolservice.getAllStudent().size();
		int total_buses = schoolservice.getAllVehicleCount();
		model.put("total_student", total_students);
		model.put("total_schools", total_schools);
		model.put("total_buses", total_buses);
		model.put("Heading_box", "Total Schools");
		session.setAttribute("new_school_id", null);
		return new ModelAndView("SuperAdmin/adminDashboard");
	}

	@RequestMapping(value = "admin/addSchool", method = RequestMethod.GET)
	public ModelAndView addSchool(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result,HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("heading", "Add School");
		model.put("countries", ControllerHelper.prepareListofBean(schoolservice.listCountry()));
		return new ModelAndView("SuperAdmin/add_school", model);
	}

	@RequestMapping(value = "admin/addSchool", method = RequestMethod.POST)
	public ModelAndView addSchool(
			@ModelAttribute("command") SchoolBean schoolbean,
			LoginBean schooladmin, BindingResult result, ModelMap model,
			HttpServletRequest request) {
		try {
			
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			
			// LoginModel found =
			// loginservice.login(schooladmin.getUser_email(),
			// "");
			LoginModel found = loginservice.checkMobile(schooladmin
					.getContact_number());

			// LoginModel
			// check=loginservice.checkUsername(schooladmin.getUser_name());
			// SchoolModel found
			// =schoolservice.checkSchoolName(schoolbean.getEmail());
			if (found == null) {
				/* This function for school information */
				String imageName = "";
				if (schoolbean.getSchool_logo() != "") {
					String filePath = schoolbean.getSchool_logo();
					String[] parts = filePath.split("base64,");
					String part2 = parts[1]; // 034556
					int rand = RandomUtils.nextInt();
					imageName = schoolbean.getSchool_name() + rand + ".png";
					String directory = Assets.SCHOOL_UPLOAD_PATH_SYS + imageName ;
					byte[] imageByteArray = Base64.decodeBase64(part2);
					// Write a image byte array into file system
					FileOutputStream imageOutFile = new FileOutputStream(
							directory);
					imageOutFile.write(imageByteArray);
					imageOutFile.close();
					
					
					
				} else {
					imageName = "";
				}

				SchoolModel schoolModal = ControllerHelper.prepareModel(schoolbean);
				schoolModal.setSchool_address(schoolbean.getSchool_address());
				schoolModal.setSchool_lat(schoolbean.getSchool_lat());
				schoolModal.setSchool_lng(schoolbean.getSchool_lng());
				schoolModal.setCountry(schoolbean.getCountry());
				schoolModal.setCity(schoolbean.getCity());
				schoolModal.setSchool_logo(imageName);
				schoolModal.setSchool_address_field(schoolbean.getSchool_address_field());
				long school_id = schoolservice.addSchool(schoolModal);

				/* This is function for school admin information */

				RandomNumber random = new RandomNumber(9999);
				String f_name = schooladmin.getFirst_name().substring(0, 1);
				String password = random.nextSessionId();
				String username = f_name + "_" + schooladmin.getLast_name();
				String username1 = f_name + "_" + schooladmin.getLast_name();
				username = username.toLowerCase();
				username1 = username1.toLowerCase();
				
				username = username.replaceAll(" ", "_").toLowerCase();
				username1 = username1.replaceAll(" ", "_").toLowerCase();
				int y = 1;
				int x = 1;
				while (y < 2) {
					LoginModel check = loginservice.checkUsername(username);
					if (check == null) {
						username = username;
						break;
					} else {
						y = 1;
						username = username1 + x;
					}
					x++;
				}
				
				LoginModel schooladminmodel = new LoginModel();
				schooladminmodel.setUser_email(schooladmin.getUser_email());
				schooladminmodel.setUser_name(username);
				schooladminmodel.setUser_pass(password);
				schooladminmodel.setSchool_id((int) school_id);
				schooladminmodel.setContact_number(schooladmin
						.getContact_number());
				schooladminmodel.setUser_role(2);
				schooladminmodel.setP_status(0);
				schooladminmodel.setFirst_name(schooladmin.getFirst_name());
				schooladminmodel.setLast_name(schooladmin.getLast_name());
				schooladminmodel.setPermission(schooladmin.getPermission());
				schooladminmodel.setMain_school_admin(1);
				schooladminmodel.setDevice_token("");
				schooladminmodel.setDevice_id("");
				schooladminmodel.setMorning_before(0);
				schooladminmodel.setEvening_before(0);
				schooladminmodel.setSms_instant_message(1);
				schooladminmodel.setSms_morning_before(1);
				schooladminmodel.setSms_evening_before(1);
				schooladminmodel.setInstant_message(0);
				schooladminmodel.setSms_wrong_route_on(1);
				
				schooladminmodel.setChat_sound(0);
				schooladminmodel.setChat_on(0);
				schoolservice.addParent(schooladminmodel);
				model.addAttribute("username", username);
				model.addAttribute("password", password);
				model.addAttribute("success", "School Added Successfully");
				model.addAttribute("school_admin_number",schooladmin.getContact_number());
				
				/*Sms_api sms = new Sms_api();
				sms.sendhttp(
						"Your school successfully addedd to tracking bus system",
						0, schooladmin.getContact_number());*/

			} else {
				model.addAttribute("schoolModel", schoolbean);
				model.addAttribute("schooladmin", schooladmin);
				model.addAttribute("error", "school already exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> modelM = new HashMap<String, Object>();
		modelM.put("countries", ControllerHelper.prepareListofBean(schoolservice.listCountry()));
		modelM.put("heading", "Add School");
		return new ModelAndView("SuperAdmin/add_school", modelM);

	}

	@RequestMapping(value = "admin/manageSchools", method = RequestMethod.GET)
	public ModelAndView allSchools(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result,HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("schools", ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
		return new ModelAndView("SuperAdmin/all_school", model);
	}

	
	
	@RequestMapping(value = "admin/manageStudents", method = RequestMethod.GET)
	public ModelAndView manageStudents(
			@ModelAttribute("command") StudentBean studentbean,
			BindingResult result, ModelMap modelH, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		modelH.addAttribute("heading", "Manage Student");
		Map<String, Object> model = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("students",
			ControllerHelper.	getStudentList(schoolservice.listStudent(school_id) , schoolservice ) );
		model.put("school_details", schoolservice.getSchoolById(school_id));
		// model.put("students", getStudentList(schoolservice.getAllStudent()));
		return new ModelAndView("SuperAdmin/all_student", model);
	}

	

	@RequestMapping(value = "admin/addStudent", method = RequestMethod.GET)
	public ModelAndView addStudent(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result, HttpServletRequest request) {
		
		
		String qr_directory =  Assets.STUDENT_QR_PATH_SYS  ;
		System.out.println(qr_directory);
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("countries", ControllerHelper. prepareListofBean(schoolservice.listCountry()));
		model.put("schools", ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		int school_id = (Integer) session.getAttribute("new_school_id");
		
		SchoolModel schol =  schoolservice.getSchoolById(school_id) ;

		model.put("school_details", schol);
		model.put("heading", "Add Student");
		model.put("parents",
				ControllerHelper. getParentList(studentservice.listParent(school_id) ,schoolservice ));
		model.put("routes", ControllerHelper.getAllRoute(schoolservice.listRoute(school_id)));
		model.put("heading", "Add Student");
		model.put("class_info",
				ControllerHelper.getAllClassList(schoolservice.getAllSchoolClasses(school_id)));
		model.put("nationality",ControllerHelper.prepareNationalityList(schoolservice.getAllNationlity()));
		model.put("country_details", schoolservice.getCountryById(schol.getCountry()));

		return new ModelAndView("SuperAdmin/add_student", model);
	}

	@RequestMapping(value = "admin/addStudent", method = RequestMethod.POST)
	public ModelAndView addStudent(
			@ModelAttribute("command") StudentBean studentbean,
			RouteLatLng latlng, RouteBean route, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		try {
			
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			SchoolModel schoolinfo = (SchoolModel) schoolservice
					.getSchoolById(studentbean.getS_school_id());

			int totalStudent = (Integer) schoolservice
					.getStudentCount(studentbean.getS_school_id());

			/*
			 * StudentModel found = studentservice.checkSchoolName(studentbean
			 * .getS_email());
			 */
			if (totalStudent > schoolinfo.getTotal_students()) {
				model.addAttribute("studentBean", studentbean);
				model.addAttribute("error", "You can not add more student");
			} else {
				/* if (found == null) { */
				String imageName = "";
				if (studentbean.getS_image_path() != "") {
					String filePath = studentbean.getS_image_path();
					String[] parts = filePath.split("base64,");
					String part2 = parts[1]; // 034556
					int rand = RandomUtils.nextInt();
					imageName = studentbean.getS_contact() + rand + ".png";
					String directory = Assets.STUDENT_UPLOAD_PATH + imageName ;
					System.out.println(directory);
					byte[] imageByteArray = Base64.decodeBase64(part2);
					// Write a image byte array into file system
					FileOutputStream imageOutFile = new FileOutputStream(
							directory);
					imageOutFile.write(imageByteArray);
					imageOutFile.close();
					

					
				} else {
					imageName = "";
				}
				StudentModel student = new StudentModel();
				student.setStudent_id(studentbean.getStudent_id());
				student.setS_fname(studentbean.getS_fname());
				student.setS_lname("");
				student.setS_email(studentbean.getS_email());
				student.setS_pass(studentbean.getS_pass());
				student.setS_address(studentbean.getS_address());
				student.setS_city(studentbean.getS_city());
				// student.setS_state(studentbean.getS_state());
				student.setS_country(studentbean.getS_country());
				student.setS_zip(studentbean.getS_zip());
				student.setS_image_path(imageName);
				student.setS_parent_id(studentbean.getS_parent_id());
				student.setS_school_id(studentbean.getS_school_id());
				student.setS_contact(studentbean.getS_contact());
				student.setP_status_id(studentbean.getP_status_id());
				student.setS_driver(studentbean.getS_driver());
				student.setS_route_id(route.getRoute_id());
				student.setFamily_name(studentbean.getFamily_name());
				student.setFather_name(studentbean.getFather_name());
				student.setGrand_name(studentbean.getGrand_name());
				student.setBlood_type(studentbean.getBlood_type());
				if(studentbean.getDob()!= null && studentbean.getDob().length() > 0) student.setDob(studentbean.getDob());
				student.setNationality(studentbean.getNationality());
				student.setGender(studentbean.getGender());
				student.setStudent_class(studentbean.getStudent_class());
				student.setStudent_id(null);
				student.setBlink_status(0);
				List<String> relationship_details=studentbean.getRelationship_details();
				List<String> parent_list=studentbean.getParent_list();
				int relation_size=relationship_details.size();
				if(relation_size==2)
				{
					relationship_details.add(2, "");
					parent_list.add(2, "0");
					
					
				}else if(relation_size==1)
				{
					//relationship_details.add("");
					relationship_details.add(1, "");
					parent_list.add(1, "0");
					relationship_details.add(2, "");
					parent_list.add(2, "0");
				}
				
				if(relationship_details.get(0)!=null && !relationship_details.get(0).equals("") )
				{
					student.setP_1(Integer.parseInt(parent_list.get(0)));
					student.setR_1(relationship_details.get(0));
				}else{
					student.setP_1(0);
					student.setR_1("");
				}
				if(relationship_details.get(1)!=null && !relationship_details.get(1).equals("") )
				{
					student.setP_2(Integer.parseInt(parent_list.get(1)));
					student.setR_2(relationship_details.get(1));
				}else{
					student.setP_2(0);
					student.setR_2("");
				}
				if(relationship_details.get(2)!=null && !relationship_details.get(2).equals("") )
				{
					student.setP_3(Integer.parseInt(parent_list.get(2)));
					student.setR_3(relationship_details.get(2));
				}else{
					student.setP_3(0);
					student.setR_3("");
				}
				
				/*System.out.println("LIST IS ="+relationship_details.get(0));
				
				
				student.setP_2(1);
				student.setP_3(1);
				
				student.setR_2("Mother");
				student.setR_3("Mother");*/
				// studentservice.addStudent(student);
				LoginModel parent = schoolservice.getParentById(studentbean
						.getS_parent_id());
				long student_id = studentservice.addNewStudent(student);
				String qr_name = "Student Id: Stud" + student_id;
				String qr_text = "Student Id: Stud" + student_id;
				qr_text += "\nStudent Name:" + studentbean.getS_fname() + " "
						+ studentbean.getS_lname();
				qr_text += "\nSchool Name:" + schoolinfo.getSchool_name();
				qr_text += "\nParent Contact:" + parent.getContact_number();
				qr_text=""+student_id;
				ByteArrayOutputStream out = QRCode.from(qr_text)
						.to(ImageType.PNG).stream();
				String qr_directory =  Assets.STUDENT_QR_PATH_SYS + "/s_" + student_id + ".png" ;

				FileOutputStream fout = new FileOutputStream(new File(
						qr_directory));
				fout.write(out.toByteArray());
				fout.flush();
				fout.close();

				RouteLatLngModel routemodel = new RouteLatLngModel();
				String lat = latlng.getLat();
				System.out.println(latlng.getLat());
				String[] latArr = lat.split(",", -1);
				String lng = latlng.getLng();
				String[] lngArr = lng.split(",", -1);
				String s_address = "";
				System.out.println("LATARR=" + latArr[latArr.length - 1]);

				GoogleResponse res1 = ControllerHelper.convertFromLatLong(latArr[latArr.length - 1]
						+ "," + lngArr[lngArr.length - 1]);
				if (res1.getStatus().equals("OK")) {
					for (Result result2 : res1.getResults()) {
						s_address = result2.getFormatted_address();
						System.out.println("address is :"
								+ result2.getFormatted_address());
					}
				} else {
					System.out.println(res1.getStatus());
				}

			
				routemodel.setId(null);
				routemodel.setSchool_id(studentbean.getS_school_id());
				routemodel.setRoute_id(route.getRoute_id());
				routemodel.setLat(latArr[latArr.length - 1]);
				routemodel.setLng(lngArr[lngArr.length - 1]);
				routemodel.setStudent_id((int) student_id);
				routemodel.setStudent_name(studentbean.getS_fname());
				routemodel.setAddress(s_address);
				routemodel.setParent_id(studentbean.getP_status_id());

				schoolservice.addRouteLatLng(routemodel);

				model.addAttribute("success", "Student Added Successfully");
				
			}
		} catch (Exception e) {
			System.out.println("HERE EXCEPTION");
			e.printStackTrace();
		}
		Map<String, Object> modelM = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("parents",
				ControllerHelper. getParentList(studentservice.listParent(school_id) ,schoolservice ));
		model.put("routes", ControllerHelper.getAllRoute(schoolservice.listRoute(school_id)));
		model.put("countries", ControllerHelper.prepareListofBean(schoolservice.listCountry()));
		model.put("schools", ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
		model.put("heading", "Add Student");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("nationality",ControllerHelper.prepareNationalityList(schoolservice.getAllNationlity()));
		model.put("class_info",
				ControllerHelper.getAllClassList(schoolservice.getAllSchoolClasses(school_id)));
		return new ModelAndView("SuperAdmin/add_student", model);

	}

	/**
	 * Function for edit student
	 **/
	@RequestMapping(value = "admin/editStudent", method = RequestMethod.GET)
	public ModelAndView editStudent(
			@ModelAttribute("command") StudentBean students,
			BindingResult result, HttpServletRequest request) {
		
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int student_id=Integer.parseInt(new String(decodedBytes));
		StudentModel studentModel = studentservice.getStudentById(student_id);
		Map<String, Object> model = new HashMap<String, Object>();
		if (studentModel != null) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			SchoolModel schol =  schoolservice.getSchoolById(school_id) ;

			model.put("school_details", schol);
			
			model.put("parents",
					ControllerHelper. getParentList(studentservice.listParent(school_id) ,schoolservice ));
			model.put("heading", "Update Student");
			model.put("countries",
					ControllerHelper.prepareListofBean(schoolservice.listCountry()));
			model.put("schools",
					ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
			model.put("studentBean", studentModel);
			model.put("routes", ControllerHelper.getAllRoute(schoolservice.listRoute(school_id)));
			RouteLatLngModel latlngmodel = studentservice
					.getLatLngBySId(student_id);
			model.put("latlng", latlngmodel);
			model.put("class_info",ControllerHelper.getAllClassList(schoolservice
					.getAllSchoolClasses(school_id)));
			model.put("nationality",ControllerHelper.prepareNationalityList(schoolservice.getAllNationlity()));
			model.put("country_details", schoolservice.getCountryById(schol.getCountry()));

			return new ModelAndView("SuperAdmin/edit_student", model);
		} else {
			return new ModelAndView("redirect:manageStudents");
		}
		}catch(Exception e)
		{	System.out.println(e);
			return new ModelAndView("redirect:manageStudents");
		}
		

	}

	/**
	 * Method for get Parent by School id
	 * **/
	@RequestMapping(value = "admin/getParentBySchoolAjax", method = RequestMethod.POST)
	public ModelAndView getParentBySchoolAjax(
			@ModelAttribute("command") StudentBean student, BindingResult result) {
		System.out.println(student.getS_parent_id());
		System.out.println(student.getS_school_id());
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("parent_id", student.getS_parent_id());
		model.put("parents",ControllerHelper. getParents( schoolservice.getParentBySchool(student.getS_school_id())));
		return new ModelAndView("SuperAdmin/get_parents", model);
	}

	/**
	 * Function for edit Student
	 **/
	@RequestMapping(value = "admin/editStudent", method = RequestMethod.POST)
	public ModelAndView editStudent(
			@ModelAttribute("command") StudentBean studentbean,
			RouteLatLng latlng, RouteBean route, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		try {
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int student_id=Integer.parseInt(new String(decodedBytes));
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			String imageName = "";
			if (studentbean.getS_image_path() != "") {

				String filePath = studentbean.getS_image_path();
				String[] parts = filePath.split("base64,");
				String part2 = parts[1]; // 034556
				int rand = RandomUtils.nextInt();
				imageName = studentbean.getS_contact() + "_" + rand + ".png";
				String directory = Assets.STUDENT_UPLOAD_PATH + imageName ;
				byte[] imageByteArray = Base64.decodeBase64(part2);
				FileOutputStream imageOutFile = new FileOutputStream(directory);
				imageOutFile.write(imageByteArray);
				imageOutFile.close();
				

				
			} else {
				imageName = "";
			}
			SchoolModel schoolinfo = (SchoolModel) schoolservice
					.getSchoolById(studentbean.getS_school_id());
		//	int student_id = student_id;
			LoginModel parent = schoolservice.getParentById(studentbean
					.getS_parent_id());
			String qr_name = "Student Id: Stud" + student_id;
			String qr_text = "Student Id: Stud" + student_id;
			qr_text += "\nStudent Name:" + studentbean.getS_fname() + " "
					+ studentbean.getS_lname();
			qr_text += "\nSchool Name:" + schoolinfo.getSchool_name();
			qr_text += "\nParent Contact:" + parent.getContact_number();
			qr_text=""+student_id;
			ByteArrayOutputStream out = QRCode.from(qr_text).to(ImageType.PNG)
					.stream();
			// String
			// qr_directory=request.getServletContext().getRealPath(Assets.STUDENT_QR_PATH);
			String qr_directory = Assets.STUDENT_QR_PATH_SYS + "/s_" + student_id + ".png" ;

			FileOutputStream fout = new FileOutputStream(new File(qr_directory));
			fout.write(out.toByteArray());
			fout.flush();
			fout.close();

			StudentModel student = new StudentModel();
			student.setS_fname(studentbean.getS_fname());
			student.setS_school_id(studentbean.getS_school_id());
			// student.setS_lname(studentbean.getS_lname());
			student.setS_lname("");
			student.setS_email(studentbean.getS_email());
			student.setS_pass(studentbean.getS_pass());
			student.setS_image_path(imageName);
			student.setS_city(studentbean.getS_city());
			student.setS_driver(studentbean.getS_driver());
			student.setS_route_id(route.getRoute_id());
			student.setS_country(studentbean.getS_country());
			student.setS_zip(studentbean.getS_zip());
			student.setS_parent_id(studentbean.getS_parent_id());
			student.setS_contact(studentbean.getS_contact());
			student.setP_status_id(studentbean.getP_status_id());
			student.setFamily_name(studentbean.getFamily_name());
			student.setBlood_type(studentbean.getBlood_type());
			student.setDob(studentbean.getDob());
			student.setFather_name(studentbean.getFather_name());
			student.setGrand_name(studentbean.getGrand_name());
			student.setNationality(studentbean.getNationality());
			student.setGender(studentbean.getGender());
			student.setStudent_class(studentbean.getStudent_class());
			student.setBlink_status(0);
			List<String> relationship_details=studentbean.getRelationship_details();
			List<String> parent_list=studentbean.getParent_list();
			int relation_size=relationship_details.size();
			
			
			if(relation_size==2)
			{
				relationship_details.add(2, "");
				parent_list.add(2, "0");
				
				
			}else if(relation_size==1)
			{
				//relationship_details.add("");
				relationship_details.add(1, "");
				parent_list.add(1, "0");
				relationship_details.add(2, "");
				parent_list.add(2, "0");
			}
			
			if(relationship_details.get(0)!=null && !relationship_details.get(0).equals("") )
			{
				student.setP_1(Integer.parseInt(parent_list.get(0)));
				student.setR_1(relationship_details.get(0));
			}else{
				student.setP_1(0);
				student.setR_1("");
			}
			if(relationship_details.get(1)!=null && !relationship_details.get(1).equals("") )
			{
				student.setP_2(Integer.parseInt(parent_list.get(1)));
				student.setR_2(relationship_details.get(1));
			}else{
				student.setP_2(0);
				student.setR_2("");
			}
			if(relationship_details.get(2)!=null && !relationship_details.get(2).equals("") )
			{
				student.setP_3(Integer.parseInt(parent_list.get(2)));
				student.setR_3(relationship_details.get(2));
			}else{
				student.setP_3(0);
				student.setR_3("");
			}
			
			
			
			
			RouteLatLngModel routemodel = new RouteLatLngModel();
			String lat = latlng.getLat();
			String[] latArr = lat.split(",", -1);
			String lng = latlng.getLng();
			String[] lngArr = lng.split(",", -1);
			String s_address = "";
			System.out.println("Status=" + latlng.getCheckStatus());
			if (latlng.getCheckStatus() == 1) {

				GoogleResponse res1 = ControllerHelper.convertFromLatLong(latArr[2] + ","
						+ lngArr[2]);
				if (res1.getStatus().equals("OK")) {
					for (Result result2 : res1.getResults()) {
						s_address = result2.getFormatted_address();
						System.out.println("address is :"
								+ result2.getFormatted_address());
					}
				} else {
					System.out.println(res1.getStatus());
				}

				routemodel.setAddress(s_address);
				routemodel.setLat(latArr[2]);
				routemodel.setLng(lngArr[2]);
				routemodel.setRoute_id(route.getRoute_id());
				student.setS_address(s_address);
				System.out.println("New=" + latArr[2]);
			} else {

				GoogleResponse res1 = ControllerHelper.convertFromLatLong(latArr[1] + ","
						+ lngArr[1]);
				if (res1.getStatus().equals("OK")) {
					for (Result result2 : res1.getResults()) {
						s_address = result2.getFormatted_address();
						System.out.println("address is :"
								+ result2.getFormatted_address());
					}
				} else {
					System.out.println(res1.getStatus());
				}
				routemodel.setAddress(s_address);
				routemodel.setLat(latArr[1]);
				routemodel.setLng(lngArr[1]);
				routemodel.setRoute_id(0);
				student.setS_address(s_address);
				System.out.println("Update=" + latArr[1]);
			}
		//	routemodel.setStudent_id(student_id);
		//	schoolservice.addRouteLatLng(routemodel);
			RouteLatLngModel routemodelCheck = new RouteLatLngModel();
			routemodelCheck= schoolservice.getStudentRouteLatLng(student_id);
			
			schoolservice.editStudentById(student_id, student);
			if(routemodelCheck!=null)
			{
				schoolservice.editRouteByStudentId(student_id,routemodel);
			}else
			{
				/*routemodel.setStudent_id(student_id);
				routemodel.setStudent_name(studentbean.getS_fname());
				routemodel.setAddress(s_address);
				routemodel.setParent_id(studentbean.getP_status_id());
				routemodel.setRoute_id(route_id);*/
				routemodel.setId(null);
				routemodel.setSchool_id(studentbean.getS_school_id());
				routemodel.setRoute_id(route.getRoute_id());
				routemodel.setLat(latArr[latArr.length - 1]);
				routemodel.setLng(lngArr[lngArr.length - 1]);
				routemodel.setStudent_id((int) student_id);
				routemodel.setStudent_name(studentbean.getS_fname());
				routemodel.setAddress(s_address);
				routemodel.setParent_id(studentbean.getP_status_id());
				
				schoolservice.addRouteLatLng(routemodel);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:manageStudents");
		}
		return new ModelAndView("redirect:manageStudents");
	}

	

	@RequestMapping(value = "admin/addParent", method = RequestMethod.GET)
	public ModelAndView addParent(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		model.addAttribute("heading", "Add Parent");
		model.put("schools",ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		SchoolModel schol = schoolservice.getSchoolById(school_id);
		model.put("country_details",
				schoolservice.getCountryById(schol.getCountry()));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/add_parent");
	}

	@RequestMapping(value = "admin/addParent", method = RequestMethod.POST)
	public ModelAndView addParent(@ModelAttribute("command") LoginBean parent,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			

			RandomNumber random = new RandomNumber(9999);
			String f_name = parent.getFirst_name().substring(0, 1);
			String password = random.nextSessionId();
			String username = parent.getFirst_name() + "."
					+ parent.getMiddle_name();
			String username1 = parent.getFirst_name() + "."
					+ parent.getMiddle_name();
			username = username.toLowerCase();
			username1 = username1.toLowerCase();
			username = username.replaceAll(" ", "_").toLowerCase();
			username1 = username1.replaceAll(" ", "_").toLowerCase();	
			int y = 1;
			int x = 1;
			while (y < 2) {
				LoginModel check = loginservice.checkUsername(username);
				if (check == null) {
					username = username;
					break;
				} else {
					y = 1;
					username = username1 + x;
				}
				x++;
			}

			// LoginModel
			// check=loginservice.checkUsername(parent.getUser_name());
			/*
			 * if(check==null) {
			 */
			LoginModel found = loginservice.checkMobile(parent.getMobile_number());
			if (found == null) {
				LoginModel parentModel = new LoginModel();
				parentModel.setUser_email(parent.getUser_email());
				parentModel.setUser_name(username);
				parentModel.setUser_pass(password);
				parentModel.setSchool_id(parent.getSchool_id());
				parentModel.setContact_number(parent.getContact_number());
				parentModel.setUser_role(3);
				parentModel.setP_status(random.generateNewRandom(5000));
				parentModel.setFirst_name(parent.getFirst_name());
				// parentModel.setLast_name(parent.getLast_name());
				parentModel.setLast_name("");
				parentModel.setDevice_token("");
				parentModel.setFamily_name(parent.getFamily_name());
				parentModel.setMiddle_name(parent.getMiddle_name());
				parentModel.setMobile_number(parent.getMobile_number());
				parentModel.setChat_on(0);
				parentModel.setLang(0);
				parentModel.setNoti_on(0);
				parentModel.setChecked_in_on(0);
				parentModel.setChecked_out_on(0);
				parentModel.setSpeed_on(1);
				parentModel.setMax_speed(100);
				parentModel.setWrong_route_on(1);
				parentModel.setSms_checked_in_on(1);
				parentModel.setSms_checked_out_on(1);
				parentModel.setSms_speed_on(1);
				parentModel.setSms_max_speed(100);
				parentModel.setSms_wrong_route_on(1);
				parentModel.setInstant_message(0);
				parentModel.setMorning_before(0);
				parentModel.setEvening_before(0);
				parentModel.setSms_instant_message(1);
				parentModel.setSms_morning_before(1);
				parentModel.setSms_evening_before(1);
				parentModel.setDevice_id("");
				parentModel.setSound_setting(0);
				parentModel.setChat_sound(0);
				schoolservice.addParent(parentModel);
				Sms_api sms = new Sms_api();
				/*
				 * sms.sendhttp("Your login details are username=" + username +
				 * " and password=" + password, 0, parent.getLast_name() +
				 * parent.getMobile_number());
				 */
				model.addAttribute("success", "Parent Added Successfully");
				model.addAttribute("username", username);
				model.addAttribute("password", password);
				model.addAttribute("heading", "Add Parent");

			} else {
				model.addAttribute("heading", "Add Parent");
				model.addAttribute("parentBean", parent);
				model.addAttribute("error", "This parent already exist");
			}
			/*
			 * }else { model.addAttribute("heading", "Add Parent");
			 * model.addAttribute("parentBean", parent);
			 * model.addAttribute("error", "This user already exist"); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpSession session = request.getSession();
		int school_id = (Integer) session.getAttribute("new_school_id");
		SchoolModel schol = schoolservice.getSchoolById(school_id);
		model.put("country_details",
				schoolservice.getCountryById(schol.getCountry()));

		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("schools", ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		return new ModelAndView("SuperAdmin/add_parent");

	}

	/**
	 * Function for manage all parents
	 **/
	@RequestMapping(value = "admin/manageParents", method = RequestMethod.GET)
	public ModelAndView manageParents(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap modelH, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		modelH.addAttribute("heading", "Manage Parents");
		Map<String, Object> model = new HashMap<String, Object>();
		/* model.put("parents", getParentList(studentservice.getAllParents())); */
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("parents",
				ControllerHelper. getParentList(studentservice.listParent(school_id) ,schoolservice ));

		return new ModelAndView("SuperAdmin/all_parents", model);
	}

	

	@RequestMapping(value = "admin/deleteParent", method = RequestMethod.GET)
	public ModelAndView editEmployee(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			HttpServletRequest request) {
		schoolservice.deleteParent(parents.getUser_id());
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("heading", "Manage Parents");
		model.put("success", "Parent Deleted Successfully");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		// model.put("parents", getParentList(studentservice.getAllParents()));
		model.put("parents",
				ControllerHelper. getParentList(studentservice.listParent(school_id) ,schoolservice ));
		return new ModelAndView("SuperAdmin/all_parents", model);
	}

	/**
	 * Function for edit parent
	 **/
	@RequestMapping(value = "admin/editParent", method = RequestMethod.GET)
	public ModelAndView editParent(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			HttpServletRequest request) {
		try{
		String parent_str=request.getParameter("parent");
		byte[] decodedBytes = Base64.decodeBase64(""+parent_str+"");
		int parent_id=Integer.parseInt(new String(decodedBytes));	
		LoginModel parent = schoolservice.getParentById(parent_id);
		String loadView = null;
		Map<String, Object> model = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		if (parent != null) {

			model.put("heading", "Edit Parent");
			model.put("schools",
					ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
			model.put("parent", parent);
			SchoolModel schol = schoolservice.getSchoolById(school_id);
			model.put("country_details",
					schoolservice.getCountryById(schol.getCountry()));
			return new ModelAndView("SuperAdmin/edit_parent", model);
		} else {
			return new ModelAndView("redirect:manageParents");
		}
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageParents");
		}

	}

	/**
	 * Function for edit parent
	 **/
	@RequestMapping(value = "admin/editParent", method = RequestMethod.POST)
	public ModelAndView editParent(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		try{
		LoginModel parentModel = new LoginModel();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		String parent_str=request.getParameter("parent");
		byte[] decodedBytes = Base64.decodeBase64(""+parent_str+"");
		int parent_id=Integer.parseInt(new String(decodedBytes));	
		int school_id = (Integer) session.getAttribute("new_school_id");
		parentModel.setContact_number(parents.getContact_number());
		parentModel.setUser_pass(parents.getUser_pass());
		parentModel.setFirst_name(parents.getFirst_name());
		parentModel.setLast_name("");
		parentModel.setFamily_name(parents.getFamily_name());
		parentModel.setMiddle_name(parents.getMiddle_name());
		parentModel.setMobile_number(parents.getMobile_number());
		parentModel.setUser_email(parents.getUser_email());
		// parentModel.setUser_name(parents.getUser_name());
		parentModel.setSchool_id(school_id);
		schoolservice.editParentById(parent_id, parentModel);
		model.put("school_details", schoolservice.getSchoolById(school_id));
		// return new ModelAndView("redirect:manageParents");
		LoginModel parent = schoolservice.getParentById(parent_id);
		model.put("heading", "Edit Parent");
		model.put("schools", ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
		model.put("parent", parent);
		SchoolModel schol = schoolservice.getSchoolById(school_id);
		model.put("country_details",
				schoolservice.getCountryById(schol.getCountry()));
		model.put("success", "Parent updated successfully");
		}catch(Exception e)
		{
			System.out.println(e);
		}
		return new ModelAndView("SuperAdmin/edit_parent", model);
	}

	/**
	 * Function for view Student
	 **/
	@RequestMapping(value = "admin/viewStudent", method = RequestMethod.GET)
	public ModelAndView viewStudent(
			@ModelAttribute("command") StudentBean students,
			BindingResult result, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int student_id=Integer.parseInt(new String(decodedBytes));	
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		StudentModel studentModel = studentservice.getStudentById(student_id);
		Map<String, Object> model = new HashMap<String, Object>();
		if (studentModel != null) {
			/*
			 * CityModel city=new CityModel(); CountryModel country=new
			 * CountryModel();
			 * city=frontService.getCityById(studentModel.getS_city());
			 */
			/* country=frontService.getCountryById(studentModel.getS_country()); */
			model.put("heading", "View Student");
			model.put("student", studentModel);
			/*
			 * model.put("city",city); model.put("country", country);
			 */
			model.put("routes", ControllerHelper.getAllRoute(schoolservice.listRoute(student_id)));
			RouteLatLngModel latlngmodel = studentservice
					.getLatLngBySId(student_id);
			model.put("allParent",ControllerHelper. getParentList(schoolservice
					.getParentByPIdList(studentModel.getP_status_id()),schoolservice));
			model.put("latlng", latlngmodel);

			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			return new ModelAndView("SuperAdmin/view_student", model);
		} else {
			
			return new ModelAndView("redirect:manageStudents");
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			return new ModelAndView("redirect:manageStudents");
		}

	}

	/**
	 * Function for view parent
	 **/
	@RequestMapping(value = "admin/viewParent", method = RequestMethod.GET)
	public ModelAndView viewParent(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			StudentBean student, HttpServletRequest request) {
		HttpSession session = request.getSession();
		LoginModel parent =new LoginModel();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try{
			
		
		String parent_str=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+parent_str+"");
		int parent_id=Integer.parseInt(new String(decodedBytes));	
		parent = schoolservice.getParentById(parent_id);
		Map<String, Object> model = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		if (parent != null) {
			model.put("allParent",ControllerHelper. getParentList(schoolservice
					.getParentByPIdList(parent.getP_status()),schoolservice));
			model.put("heading", "View Parent");
			model.put("parent", parent);
			// model.put("students",
			// getStudentList(studentservice.getStudentByParent(parents.getUser_id())));
			model.put("students",ControllerHelper. getStudentList(studentservice
					.getStudentByParentStatus(parent.getP_status()) , schoolservice));

			return new ModelAndView("SuperAdmin/view_parent", model);
		} else {
			return new ModelAndView("redirect:manageParents");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:manageParents");
		}

	}

	@RequestMapping(value = "admin/deleteStudent", method = RequestMethod.GET)
	public ModelAndView deleteStudent(
			@ModelAttribute("command") StudentBean student,
			BindingResult result, ModelMap modelH) {
		schoolservice.deleteStudent(student.getStudent_id());
		return new ModelAndView("redirect:manageStudents");
	}

	/**
	 * Function for manage Driver
	 **/
	@RequestMapping(value = "admin/manageDrivers", method = RequestMethod.GET)
	public ModelAndView manageDrivers(
			@ModelAttribute("command") DriverBean driverbean,
			BindingResult result, ModelMap modelH, HttpServletRequest request,HttpServletResponse response) {
		modelH.addAttribute("heading", "Manage Driver");
		Map<String, Object> model = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try{
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("drivers",
				ControllerHelper. getDriversList(schoolservice.listDriver(school_id) , schoolservice));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		// model.put("drivers", getDriversList(schoolservice.allDrivers()));
		
		}catch(Exception e)
		{
			System.out.println(e);
		}
		return new ModelAndView("SuperAdmin/all_driver", model);
		
	}

	

	/**
	 * Function for edit student
	 **/
	@RequestMapping(value = "admin/editDriver", method = RequestMethod.GET)
	public ModelAndView editVechile(
			@ModelAttribute("command") DriverBean driverbean,
			BindingResult result, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int driver_id=Integer.parseInt(new String(decodedBytes));	
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		DriverModel drivermodel = schoolservice.getDriverById(driver_id);
		Map<String, Object> model = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		if (drivermodel != null) {
			model.put("heading", "Edit Driver");
			model.put("driver", drivermodel);
			model.put("routes",
					ControllerHelper.getAllRoute(schoolservice.listRoute((Integer) session
							.getAttribute("new_school_id"))));
			model.put("schools",
					ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
			SchoolModel schol = schoolservice.getSchoolById(school_id);
			model.put("country_details",
					schoolservice.getCountryById(schol.getCountry()));
			model.put("documents", ControllerHelper.prepareListOfDriverDoc(schoolservice.getDriverDocument(driver_id)));
			model.put("nationality",ControllerHelper.prepareNationalityList(schoolservice.getAllNationlity()));
			return new ModelAndView("SuperAdmin/edit_driver", model);
		} else {
			return new ModelAndView("redirect:manageDrivers");
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			return new ModelAndView("redirect:manageDrivers");
		}

	}

	/**
	 * Function for edit Driver Post Method
	 **/
	@RequestMapping(value = "admin/editDriver", method = RequestMethod.POST)
	public ModelAndView editDriver(
			@ModelAttribute("command") DriverBean driverbean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int driver_id=Integer.parseInt(new String(decodedBytes));
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			String imageName = "";
			if (!driverbean.getImage_path().equals("")) {
				String filePath = driverbean.getImage_path();
				String[] parts = filePath.split("base64,");
				String part2 = parts[1]; // 034556
				int rand = RandomUtils.nextInt();
				imageName = driverbean.getContact_number() + rand + ".png";
				String directory = Assets.DRIVER_UPLOAD_PATH + imageName ;
				byte[] imageByteArray = Base64.decodeBase64(part2);
				System.out.println(directory);
				FileOutputStream imageOutFile = new FileOutputStream(directory);
				imageOutFile.write(imageByteArray);
				imageOutFile.close();
				
				
				
			} else {
				imageName = "";

				System.out.println(driverbean.getImage_path());
			}

			DriverModel driver = new DriverModel();
			driver.setAddress(driverbean.getAddress());
			driver.setDriver_fname(driverbean.getDriver_fname());
			driver.setDriver_lname(driverbean.getDriver_lname());
			// driver.setContact_number(driverbean.getContact_number());
			driver.setD_email(driverbean.getD_email());
			driver.setDriver_school_id(driverbean.getDriver_school_id());
			driver.setImage_path(imageName);
			driver.setMiddle_name(driverbean.getMiddle_name());
			driver.setDob(driverbean.getDob());
			driver.setNationality(driverbean.getNationality());
			if(driverbean.getLicence_expiry()!="" && !driverbean.getLicence_expiry().equals(""))
			{
				driver.setLicence_expiry(driverbean.getLicence_expiry());
			}
			else
			{
				driver.setLicence_expiry("0000-00-00");
			}
			//driver.setLicence_expiry(driverbean.getLicence_expiry());
			driver.setBlood_group(driverbean.getBlood_group());
			if (driverbean.getRoute_id() != 0) {
				driver.setRoute_id(driverbean.getRoute_id());
			} else {
				driver.setRoute_id(0);
			}
			schoolservice.editDriverById(driver_id, driver);
			
			List<String> insurance_document_name=driverbean.getInsurance_document_name();
			List<String> insurance_document_expiry=driverbean.getInsurance_document_expiry();
			List<String> insurance_end_date=driverbean.getInsurance_end_date();
			List<String> insurance_remind=driverbean.getRemind_day();
			List<MultipartFile> files = driverbean.getInsurance_card_copy();
			
			String new_file_name = "";

			if (null != files && files.size() > 0) {
				for (MultipartFile multipartFile : files) {
					 System.out.println("File name="+multipartFile.getOriginalFilename());

					
					if (multipartFile.getOriginalFilename() != null && multipartFile.getOriginalFilename() != "") {
						String fileName = multipartFile.getOriginalFilename();

						if (!fileName.isEmpty()) {
							String[] fn = fileName.toString().split("\\.");
							int rand = RandomUtils.nextInt();
							String directory = 
											Assets.INSURANCE_CARD_COPY_SYS+rand + fn[0] + "." + fn[1] ;
							new_file_name += rand + fn[0] + "." + fn[1]
									+ ",";
							System.out.println(directory);
							BufferedOutputStream stream = new BufferedOutputStream(
									new FileOutputStream(directory));
							stream.write(multipartFile.getBytes());
							stream.close();
						} else {
							driver.setInsurance_card_copy("");
							new_file_name +=",";
						}
					}else
					{
						new_file_name +=",";
					}

				}
			}else
			{
				new_file_name +=",";
			}
			
			
			int j=0;
			List<String> v_doc_id=driverbean.getV_doc_id();
			List<String> items = Arrays.asList(new_file_name.split("\\s*,\\s*"));
			if(insurance_document_name!=null)
			{
			for(String a : insurance_document_name)
			{
				if(!v_doc_id.get(j).equals("0"))
				{
					DriverDocModel doc=new DriverDocModel();
					doc.setInsurance_document_name(a);
					doc.setInsurance_document_expiry(insurance_document_expiry.get(j));
					doc.setInsurance_end_date(insurance_end_date.get(j));
					doc.setRemind_day(insurance_remind.get(j));
					/*if(!items.get(j).equals(""))
					{*/
					if(items.get(j).length()>0)
					{
						doc.setInsurance_card_copy(items.get(j));
					}
					else
					{
						doc.setInsurance_card_copy("");
					}
					
					vechileservice.editDriverDocById(Integer.parseInt(v_doc_id.get(j)), doc);
					System.out.println("E#");
				}else
				{
					DriverDocModel doc=new DriverDocModel();
					doc.setInsurance_document_name(a);
					doc.setInsurance_document_expiry(insurance_document_expiry.get(j));
					doc.setRemind_day(insurance_remind.get(j));
					doc.setV_id((int) driver_id);
					doc.setSchool_id((Integer) session.getAttribute("new_school_id"));
					doc.setInsurance_card_copy(items.get(j));
					doc.setStatus(0);
					doc.setNoti_type(1);
					doc.setInsurance_end_date(insurance_end_date.get(j));
					if(insurance_document_expiry.get(j)!="" && !insurance_document_expiry.get(j).equals(""))
					{
						vechileservice.addDriverDoc(doc);
					}
					
				}
				j++;
			}	
	      }	
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:manageDrivers");
		}
		//return new ModelAndView("redirect:manageDrivers");
		try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int driver_id=Integer.parseInt(new String(decodedBytes));	
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			DriverModel drivermodel = schoolservice.getDriverById(driver_id);
			//Map<String, Object> model = new HashMap<String, Object>();
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			if (drivermodel != null) {
				model.put("heading", "Edit Driver");
				model.put("driver", drivermodel);
				model.put("routes",
						ControllerHelper.getAllRoute(schoolservice.listRoute((Integer) session
								.getAttribute("new_school_id"))));
				model.put("schools",
						ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
				SchoolModel schol = schoolservice.getSchoolById(school_id);
				model.put("country_details",
						schoolservice.getCountryById(schol.getCountry()));
				model.put("documents", ControllerHelper.prepareListOfDriverDoc(schoolservice.getDriverDocument(driver_id)));
				model.put("nationality",ControllerHelper.prepareNationalityList(schoolservice.getAllNationlity()));
				model.addAttribute("success", "Driver updated successfully");
				return new ModelAndView("SuperAdmin/edit_driver", model);
			} else {
				return new ModelAndView("redirect:manageDrivers");
			}
			}catch(Exception e)
			{
				e.printStackTrace();
				return new ModelAndView("redirect:manageDrivers");
			}
	}

	/**
	 * Function for view driver
	 **/
	@RequestMapping(value = "admin/viewDriver", method = RequestMethod.GET)
	public ModelAndView viewDriver(
			@ModelAttribute("command") DriverBean driverbean,
			BindingResult result, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int driver_id=Integer.parseInt(new String(decodedBytes));	
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		DriverModel drivermodel = schoolservice.getDriverById(driver_id);
		RouteModel route = schoolservice.getRouteById(drivermodel.getRoute_id());
		Map<String, Object> model = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		if (drivermodel != null) {
			model.put("heading", "View Driver");
			model.put("driver", drivermodel);
			model.put("route", route);
			model.put("routes",
					ControllerHelper.getAllRoute(schoolservice.listRoute((Integer) session
							.getAttribute("new_school_id"))));
			model.put("schools",
					ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
			String ed=request.getParameter("v");
			if(ed != null)
			{
				byte[] decodedBytes1 = Base64.decodeBase64(""+ed+"");
				schoolservice.updateHeadNotification(Integer.parseInt(new String(decodedBytes1)));
				
			}
			model.put("documents",ControllerHelper. prepareListOfDriverDoc(schoolservice.getDriverDocument(driver_id)));
			return new ModelAndView("SuperAdmin/view_driver", model);
		} else {
			return new ModelAndView("redirect:manageDrivers");
		}
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageDrivers");
		}

	}

	/**
	 * Function for add Driver
	 * */
	@RequestMapping(value = "admin/addDriver", method = RequestMethod.GET)
	public ModelAndView addDriver(
			@ModelAttribute("command") DriverBean driverbean,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("heading", "Add Driver");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("schools",ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		model.put("routes", ControllerHelper.getAllRoute(schoolservice.listRoute(school_id)));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("nationality",ControllerHelper.prepareNationalityList(schoolservice.getAllNationlity()));
		SchoolModel schol = schoolservice.getSchoolById(school_id);
		model.put("country_details",schoolservice.getCountryById(schol.getCountry()));

		return new ModelAndView("SuperAdmin/add_driver", model);
	}

	/**
	 * Function for add staff post method
	 **/
	@RequestMapping(value = "admin/addDriver", method = RequestMethod.POST)
	public ModelAndView addDriver(
			@ModelAttribute("command") DriverBean driverbean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session=request.getSession();
			DriverModel found = schoolservice.checkDriverContact(driverbean
					.getContact_number());
			RandomNumber random = new RandomNumber(9999);
			String f_name = driverbean.getDriver_fname().substring(0, 1);
			int password = random.generateNewRandom(5000);// random.nextSessionId();
			String username = f_name + "." + driverbean.getDriver_lname();
			String username1 = f_name + "." + driverbean.getDriver_lname();
			username = username.toLowerCase();
			username1 = username1.toLowerCase();
			username = username.replaceAll(" ", "_").toLowerCase();
			username1 = username1.replaceAll(" ", "_").toLowerCase();	
			int y = 1;
			int x = 1;
			while (y < 2) {
				DriverModel check = schoolservice.checkDriverUserName(username);
				if (check == null) {
					username = username;
					break;
				} else {
					y = 1;
					username = username1 + x;
				}
				x++;
			}

			if (found == null) {
				String imageName = "";
				
				if (driverbean.getImage_path() != "" && !driverbean.getImage_path().equals("")) {
					String filePath = driverbean.getImage_path();
					String[] parts = filePath.split("base64,");
					String part2 = parts[1]; // 034556
					int rand = RandomUtils.nextInt();
					imageName = driverbean.getContact_number() + rand + ".png";
					String directory =  Assets.DRIVER_UPLOAD_PATH + imageName ;
					byte[] imageByteArray = Base64.decodeBase64(part2);
					// Write a image byte array into file system
					FileOutputStream imageOutFile = new FileOutputStream(
							directory);
					imageOutFile.write(imageByteArray);
					imageOutFile.close();
					
					
				} else {
					imageName = "";
				}

				DriverModel drivermodel = new DriverModel();
				drivermodel.setAddress(driverbean.getAddress());
				drivermodel.setContact_number(driverbean.getContact_number());
				drivermodel.setD_email(driverbean.getD_email());
				drivermodel.setDriver_fname(driverbean.getDriver_fname());
				drivermodel.setDriver_id(null);
				drivermodel.setUsername(username);
				drivermodel.setDriver_lname(driverbean.getDriver_lname());
				drivermodel.setDriver_school_id(driverbean
						.getDriver_school_id());
				drivermodel.setImage_path(imageName);
				drivermodel.setPassword("" + password);
				drivermodel.setStatus(1);
				drivermodel.setMiddle_name(driverbean.getMiddle_name());
				drivermodel.setDevice_id("");
				drivermodel.setLang(0);
				drivermodel.setLogged_status(0);
				if(driverbean.getDob()!="" && !driverbean.getDob().equals(""))
				{
					drivermodel.setDob(driverbean.getDob());
				}else
				{
					drivermodel.setDob("0000-00-00");
				}
				drivermodel.setNationality(driverbean.getNationality());
				if(driverbean.getLicence_expiry()!="" && !driverbean.getLicence_expiry().equals(""))
				{
					drivermodel.setLicence_expiry(driverbean.getLicence_expiry());
				}else{
					drivermodel.setLicence_expiry("0000-00-00");
				}
				
				drivermodel.setBlood_group(driverbean.getBlood_group());
				drivermodel.setInsurance_card_copy("");
				drivermodel.setRemind_day("");
				drivermodel.setInsurance_document_expiry("");
				drivermodel.setInsurance_document_name("");
				
				// drivermodel.setRoute_id(driverbean.getRoute_id());

				if (driverbean.getRoute_id() != 0) {
					drivermodel.setRoute_id(driverbean.getRoute_id());
				} else {
					drivermodel.setRoute_id(0);
				}
				long insert_id = schoolservice.addDriver(drivermodel);

				String qr_text = "" + insert_id + "";
				ByteArrayOutputStream out = QRCode.from(qr_text)
						.to(ImageType.PNG).stream();
				String qr_directory = Assets.STUDENT_QR_PATH_SYS + "d_" + insert_id + ".png" ;

				FileOutputStream fout = new FileOutputStream(new File(
						qr_directory));
				fout.write(out.toByteArray());
				fout.flush();
				fout.close();

				/*
				 * Sms_api sms = new Sms_api();
				 * sms.sendhttp("Your login details are username=" + username +
				 * " and password=" + password, 0,
				 * driverbean.getContact_number());
				 */
				List<String> fileNames = new ArrayList<String>();
				List<String> insurance_document_name=driverbean.getInsurance_document_name();
				List<String> insurance_document_expiry=driverbean.getInsurance_document_expiry();
				List<String> insurance_remind=driverbean.getRemind_day();
				List<String> insurance_end_date=driverbean.getInsurance_end_date();
				String in_doc_name="";
				for(String a : insurance_document_name)
				{
					in_doc_name +=a+",";
				}
				String in_doc_date="";
				for(String a : insurance_document_expiry)
				{
					in_doc_date +=a+",";
				}
				String in_doc_remind="";
				for(String a : insurance_remind)
				{
					in_doc_remind +=a+",";
				}
				String in_end_date="";
				for(String a : insurance_end_date)
				{
					in_end_date +=a+",";
				}
				List<MultipartFile> files = driverbean.getInsurance_card_copy();
				String new_file_name = "";
				if (null != files && files.size() > 0) {
					for (MultipartFile multipartFile : files) {
						System.out.println(multipartFile.getSize());
						
						String fileName = multipartFile.getOriginalFilename();
						// fileNames.add(fileName);
						if (!fileName.isEmpty()) {
							byte[] bytes = fileName.getBytes();
							System.out.println(fileName.toString());
							String[] fn = fileName.toString().split("\\.");
							int rand = RandomUtils.nextInt();
							String directory = 
											Assets.INSURANCE_CARD_COPY_SYS
													+ rand
													+ driverbean
															.getDriver_fname()
													+ "." + fn[1] ;
							new_file_name += rand
									+ driverbean.getDriver_fname() + "."
									+ fn[1] + ",";
							BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(directory));
							stream.write(multipartFile.getBytes());
							stream.close();
						} else {
							drivermodel.setInsurance_card_copy("");
						}

					}
				}
				
				drivermodel.setInsurance_card_copy(new_file_name);
				drivermodel.setInsurance_document_name(in_doc_name);
				drivermodel.setInsurance_document_expiry(in_doc_date);
				drivermodel.setRemind_day(in_doc_remind);
				long vechile_id=insert_id;
				
				int j=0;
				List<String> items = Arrays.asList(new_file_name.split("\\s*,\\s*"));
				for(String a : insurance_document_name)
				{
					DriverDocModel doc=new DriverDocModel();
					doc.setInsurance_document_name(a);
					doc.setInsurance_document_expiry(insurance_document_expiry.get(j));
					doc.setRemind_day(insurance_remind.get(j));
					doc.setInsurance_end_date(insurance_end_date.get(j));
					doc.setV_id((int) vechile_id);
					doc.setSchool_id((Integer) session.getAttribute("new_school_id"));
					doc.setInsurance_card_copy(items.get(j));
					doc.setStatus(0);
					doc.setNoti_type(1);
					if(insurance_document_expiry.get(j)!="" && !insurance_document_expiry.get(j).equals(""))
					{
						vechileservice.addDriverDoc(doc);
					}
					j++;
				}


				
				
				model.addAttribute("driver_contact",driverbean.getContact_number());
				driverbean = null;
				model.addAttribute("username", username);
				model.addAttribute("password", password);
				
				model.addAttribute("heading", "Add Driver");
				
				model.addAttribute("success", "Driver Added Successfully");
			} else {
				model.addAttribute("driver", driverbean);
				model.addAttribute("error", "This Driver already exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.put("driver", driverbean);
		model.put("heading", "Add Driver");
		HttpSession session = request.getSession();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("routes", ControllerHelper.getAllRoute(schoolservice.listRoute(school_id)));
		SchoolModel schol = schoolservice.getSchoolById(school_id);
		model.put("country_details",
				schoolservice.getCountryById(schol.getCountry()));
		model.put("nationality",ControllerHelper.prepareNationalityList(schoolservice.getAllNationlity()));
		model.put("schools", ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
		return new ModelAndView("SuperAdmin/add_driver", model);

	}

	/*
	 * Function for delete
	 */
	@RequestMapping(value = "admin/deleteDriver", method = RequestMethod.GET)
	public ModelAndView deleteDriver(
			@ModelAttribute("command") DriverBean driverbean,
			BindingResult result) {

		schoolservice.deleteDriver(driverbean.getDriver_id());
		return new ModelAndView("redirect:manageDrivers");
	}

	/**
	 * Function for manage vehicle
	 * **/
	@RequestMapping(value = "admin/manageVehicle", method = RequestMethod.GET)
	public ModelAndView manageVechile(
			@ModelAttribute("command") VechileBean vechilebean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try{
		model.addAttribute("heading", "Manage Vehicle");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("vechiles",
				ControllerHelper.getAllVechiles(vechileservice.getVechile(school_id), schoolservice));
		// model.put("vechiles",
		// getAllVechiles(vechileservice.getAllVehicles()));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return new ModelAndView("SuperAdmin/all_vechiles");
	}

	

	/**
	 * Function for add New Vehicles
	 * **/
	@RequestMapping(value = "admin/addVehicle", method = RequestMethod.GET)
	public ModelAndView addVechile(
			@ModelAttribute("command") VechileBean vechilebean,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		try {
			model.addAttribute("heading", "Add Vehicle");
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("drivers",
					ControllerHelper. getDriversList(schoolservice.listDriver(school_id) , schoolservice));
			model.put("school_details", schoolservice.getSchoolById(school_id));
			model.put("schools",
					ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("SuperAdmin/add_vechile");
	}

	/**
	 * Function for add vehicles post method
	 * **/
	@RequestMapping(value = "admin/addVehicle", method = RequestMethod.POST)
	public ModelAndView addVechile(
			@ModelAttribute("command") VechileBean vechileBean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			VehicleModel vechile_check = new VehicleModel();
			vechile_check = schoolservice.getVehicleCheck(vechileBean
					.getBus_number());
			VehicleModel vechile = new VehicleModel();
			if (vechile_check != null) {
				model.addAttribute("vechileBean", vechile);
				model.addAttribute("error", "Vechile already exist");
			} else {
				SchoolModel schoolinfo = (SchoolModel) schoolservice
						.getSchoolById(vechileBean.getSchool_id());
				int totalVehicle = (Integer) schoolservice
						.getVehicleCount(vechileBean.getSchool_id());
				if (totalVehicle >= schoolinfo.getTotal_bus()) {

					vechile.setBus_number(vechileBean.getBus_number());
					vechile.setColor(vechileBean.getColor());
					vechile.setConfigurtion(vechileBean.getConfigurtion());
					vechile.setEngine(vechileBean.getEngine());
					vechile.setManufacture(vechileBean.getManufacture());
					vechile.setModel(vechileBean.getModel());
					vechile.setSchool_id(vechileBean.getSchool_id());
					vechile.setVehile_name(vechileBean.getVehile_name());
					vechile.setVechile_id(null);
					vechile.setYear(vechileBean.getYear());
					model.addAttribute("vechileBean", vechile);
					model.addAttribute("error", "You can not add more busses");
				} else {

					vechile.setBus_number(vechileBean.getBus_number());
					vechile.setColor(vechileBean.getColor());
					vechile.setConfigurtion(vechileBean.getConfigurtion());
					vechile.setEngine(vechileBean.getEngine());
					vechile.setManufacture(vechileBean.getManufacture());
					vechile.setModel(vechileBean.getModel());
					vechile.setSchool_id(vechileBean.getSchool_id());
					vechile.setVehile_name(vechileBean.getVehile_name());
					vechile.setVechile_id(null);
					vechile.setYear(vechileBean.getYear());
					/*vechile.setInsurance_end_date(vechileBean
							.getInsurance_end_date());*/
					vechile.setInsurance_end_date("0000-00-00");
					// MultipartFile file =
					// vechileBean.getInsurance_card_copy();
					
					List<String> fileNames = new ArrayList<String>();
					List<String> insurance_document_name=vechileBean.getInsurance_document_name();
					List<String> insurance_document_expiry=vechileBean.getInsurance_document_expiry();
					List<String> insurance_remind=vechileBean.getRemind_day();
					List<String> insrance_end_date_e=vechileBean.getInsurance_end_date_e();
					String in_end_date="";
					for(String a : insrance_end_date_e)
					{
						in_end_date +=a+",";
					}
					String in_doc_name="";
					for(String a : insurance_document_name)
					{
						in_doc_name +=a+",";
					}
					String in_doc_date="";
					for(String a : insurance_document_expiry)
					{
						in_doc_date +=a+",";
					}
					
					String in_doc_remind="";
					for(String a : insurance_remind)
					{
						in_doc_remind +=a+",";
					}
					List<MultipartFile> files = vechileBean.getInsurance_card_copy();
					/*String new_file_name = "";
					if (null != files && files.size() > 0) {
						for (MultipartFile multipartFile : files) {

							String fileName = multipartFile.getOriginalFilename();
							// fileNames.add(fileName);
							if (!fileName.isEmpty()) {
								byte[] bytes = fileName.getBytes();

								String[] fn = fileName.toString().split("\\.");
								int rand = RandomUtils.nextInt();
								String directory = request
										.getServletContext()
										.getRealPath(
												Assets.INSURANCE_CARD_COPY
														+ rand
														+ vechileBean
																.getVehile_name()
														+ "." + fn[1]);
								new_file_name += rand
										+ vechileBean.getVehile_name() + "."
										+ fn[1] + ",";
								BufferedOutputStream stream = new BufferedOutputStream(
										new FileOutputStream(directory));
								System.out.println(fileName.getBytes());
								stream.write(bytes);
								stream.close();
							} else {
								vechile.setInsurance_card_copy("");
							}

						}
					}*/
					String new_file_name = "";
					if (null != files && files.size() > 0) {
						for (MultipartFile multipartFile : files) {
							System.out.println(multipartFile.getSize());
							
							String fileName = multipartFile.getOriginalFilename();
							// fileNames.add(fileName);
							if (!fileName.isEmpty()) {
								byte[] bytes = fileName.getBytes();
								System.out.println(fileName.toString());
								String[] fn = fileName.toString().split("\\.");
								int rand = RandomUtils.nextInt();
								String directory = 
												Assets.INSURANCE_CARD_COPY_SYS
														+ rand
														+ vechileBean
																.getVehile_name()
														+ "." + fn[1];
								new_file_name += rand
										+ vechileBean.getVehile_name() + "."
										+ fn[1] + ",";
								BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(directory));
								stream.write(multipartFile.getBytes());
								stream.close();
							} else {
								vechile.setInsurance_card_copy("");
							}

						}
					}

					/*
					 * String[] fn = file.getOriginalFilename().split("\\.");
					 * vechile.setInsurance_end_date(vechileBean
					 * .getInsurance_end_date()); int rand =
					 * RandomUtils.nextInt(); if (!file.isEmpty()) { byte[]
					 * bytes = file.getBytes();
					 * 
					 * String rootPath = System.getProperty("catalina.home");
					 * File dir = new File(rootPath + File.separator +
					 * "tmpFiles");
					 * 
					 * String directory =
					 * request.getServletContext().getRealPath(
					 * Assets.INSURANCE_CARD_COPY + rand +
					 * vechileBean.getVehile_name() + "." + fn[1]);
					 * 
					 * BufferedOutputStream stream = new BufferedOutputStream(
					 * new FileOutputStream(directory)); stream.write(bytes);
					 * stream.close();
					 * 
					 * vechile.setInsurance_card_copy(rand +
					 * vechileBean.getVehile_name() + "." + fn[1]);
					 * 
					 * } else { vechile.setInsurance_card_copy(""); }
					 */
					vechile.setInsurance_card_copy(new_file_name);
					vechile.setInsurance_document_name(in_doc_name);
					vechile.setInsurance_document_expiry(in_doc_date);
					vechile.setRemind_day(in_doc_remind);
					vechile.setDriver_id(vechileBean.getDriver_id());
					long vechile_id=vechileservice.addVechile(vechile);
					
					int j=0;
					System.out.println("here");
					List<String> items = Arrays.asList(new_file_name.split("\\s*,\\s*"));
					for(String a : insurance_document_name)
					{
						VehicleDocModel doc=new VehicleDocModel();
						doc.setInsurance_document_name(a);
						doc.setInsurance_document_expiry(insurance_document_expiry.get(j));
						doc.setRemind_day(insurance_remind.get(j));
						doc.setInsurance_end_date(insrance_end_date_e.get(j));
						doc.setV_id((int) vechile_id);
						doc.setSchool_id((Integer) session.getAttribute("new_school_id"));
						doc.setInsurance_card_copy(items.get(j));
						doc.setStatus(0);
						doc.setNoti_type(0);
						if(insurance_document_expiry.get(j)!="" && !insurance_document_expiry.get(j).equals(""))
						{
							System.out.println("Test="+j);
							vechileservice.addVechileDoc(doc);
						}
						j++;
					}
					vechile = null;
					model.addAttribute("vechileBean", vechile);
					model.put("school_details", schoolservice.getSchoolById((Integer) session.getAttribute("new_school_id")));
					model.put("drivers",
						ControllerHelper.	getDriversList(schoolservice.listDriver((Integer) session.getAttribute("new_school_id")), schoolservice));
					model.addAttribute("success", "Vechile Added Successfully");

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e);

		}
		HttpSession session = request.getSession();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("schools", ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		model.addAttribute("heading", "Add Vehicle");
		return new ModelAndView("SuperAdmin/add_vechile");

	}

	/**
	 * Function for edit vehicle
	 **/
	@RequestMapping(value = "admin/editVechile", method = RequestMethod.GET)
	public ModelAndView editVechile(
			@ModelAttribute("command") VechileBean vechile,
			BindingResult result, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int vehicle_id=Integer.parseInt(new String(decodedBytes));
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		VehicleModel vehiclemodel = vechileservice.getVehicleById(vehicle_id);
		Map<String, Object> model = new HashMap<String, Object>();
		if (vehiclemodel != null) {
			model.put("heading", "Edit Vehicle");
			model.put("vechileBean", vehiclemodel);
			model.put("schools",
					ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			model.put("documents",ControllerHelper. prepareListOfVehicleDoc(schoolservice.getVehicleDocument(vehiclemodel.getVechile_id())));
			
			String ed=request.getParameter("v");
			if(ed != null)
			{
				byte[] decodedBytes1 = Base64.decodeBase64(""+ed+"");
				schoolservice.updateHeadNotification(Integer.parseInt(new String(decodedBytes1)));
				
			}
			model.put("drivers",
					ControllerHelper. getDriversList(schoolservice.listDriver((Integer) session.getAttribute("new_school_id")) , schoolservice));
			return new ModelAndView("SuperAdmin/edit_vehicle", model);
		} else {
			return new ModelAndView("redirect:manageVehicle");
		}
		}catch(Exception e)
		{
			
			return new ModelAndView("redirect:manageVehicle");
		}

	}

	/**
	 * Function for edit Vehicle
	 **/
	@RequestMapping(value = "admin/editVechile", method = RequestMethod.POST)
	public ModelAndView editVechile(
			@ModelAttribute("command") VechileBean vechile,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int vehicle_id=Integer.parseInt(new String(decodedBytes));
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			SchoolModel schoolinfo = (SchoolModel) schoolservice
					.getSchoolById(vechile.getSchool_id());
			int totalVehicle = (Integer) schoolservice.getVehicleCount(vechile
					.getSchool_id());
			if (totalVehicle > schoolinfo.getTotal_bus()) {
				VehicleModel vehiclemodel = new VehicleModel();
				vehiclemodel.setBus_number(vechile.getBus_number());
				vehiclemodel.setColor(vechile.getColor());
				vehiclemodel.setConfigurtion(vechile.getConfigurtion());
				vehiclemodel.setEngine(vechile.getEngine());
				vehiclemodel.setManufacture(vechile.getManufacture());
				vehiclemodel.setModel(vechile.getModel());
				vehiclemodel.setVechile_id(vehicle_id);
				vehiclemodel.setVehile_name(vechile.getVehile_name());
				vehiclemodel.setYear(vechile.getYear());
				vehiclemodel.setSchool_id(vechile.getSchool_id());
				model.addAttribute("vechileBean", vechile);
				model.put("schools",
						ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
				model.addAttribute("error", "You can not add more busses");
				int school_id = (Integer) session.getAttribute("new_school_id");
				model.put("school_details",
						schoolservice.getSchoolById(school_id));
				return new ModelAndView("SuperAdmin/edit_vehicle");
			} else {
				VehicleModel vehiclemodel = new VehicleModel();
				vehiclemodel.setBus_number(vechile.getBus_number());
				vehiclemodel.setColor("");
				vehiclemodel.setConfigurtion(vechile.getConfigurtion());
				vehiclemodel.setEngine(vechile.getEngine());
				vehiclemodel.setManufacture(vechile.getManufacture());
				vehiclemodel.setModel(vechile.getModel());
				vehiclemodel.setVechile_id(vechile.getVechile_id());
				vehiclemodel.setVehile_name(vechile.getVehile_name());
				vehiclemodel.setYear(vechile.getYear());
				vehiclemodel.setSchool_id(vechile.getSchool_id());
				vehiclemodel.setDriver_id(vechile.getDriver_id());
				/*vehiclemodel.setInsurance_end_date(vechile
						.getInsurance_end_date());*/
				vehiclemodel.setInsurance_end_date("0000-00-00");
				// MultipartFile file = vechileBean.getInsurance_card_copy();
				List<String> insurance_document_name=vechile.getInsurance_document_name();
				List<String> insurance_document_expiry=vechile.getInsurance_document_expiry();
				List<String> insurance_remind=vechile.getRemind_day();
				List<String> insurance_end_date=vechile.getInsurance_end_date_e();
				String in_end_date="";
				
				if(insurance_end_date!=null){
				for(String a : insurance_end_date)
				{
					in_end_date +=a+",";
				}
				}
				String in_doc_name="";
				
				if(insurance_document_name!=null){
				for(String a : insurance_document_name)
				{
					in_doc_name +=a+",";
				}
				}
				
				String in_doc_date="";
				
				if(insurance_document_expiry!=null){
				for(String a : insurance_document_expiry)
				{
					in_doc_date +=a+",";
				}
				}
				
				String in_doc_remind="";
				if(insurance_remind!=null){
				for(String a : insurance_remind)
				{
					in_doc_remind +=a+",";
				}
				}
				List<MultipartFile> files = vechile.getInsurance_card_copy();
				List<String> fileNames = new ArrayList<String>();
				MultipartFile abc = null;
				String new_file_name = "";

				if (null != files && files.size() > 0) {
					for (MultipartFile multipartFile : files) {
						 System.out.println("File name="+multipartFile.getOriginalFilename());

						
						if (multipartFile.getOriginalFilename() != null && multipartFile.getOriginalFilename() != "") {
							String fileName = multipartFile
									.getOriginalFilename();

							if (!fileName.isEmpty()) {
								byte[] bytes = fileName.getBytes();
								String[] fn = fileName.toString().split("\\.");
								int rand = RandomUtils.nextInt();
								String directory = 
												Assets.INSURANCE_CARD_COPY_SYS+rand + fn[0] + "." + fn[1] ;
								new_file_name += rand + fn[0] + "." + fn[1]
										+ ",";
								System.out.println(directory);
								BufferedOutputStream stream = new BufferedOutputStream(
										new FileOutputStream(directory));
								stream.write(multipartFile.getBytes());
								stream.close();
							} else {
								vehiclemodel.setInsurance_card_copy("");
								new_file_name +=",";
							}
						}else
						{
							new_file_name +=",";
						}

					}
				}else
				{
					new_file_name +=",";
				}
				
				vehiclemodel.setInsurance_card_copy(vechile.getColor()
						+ new_file_name);
				
				vehiclemodel.setInsurance_document_name(in_doc_name);
				vehiclemodel.setInsurance_document_expiry(in_doc_date);
				vehiclemodel.setRemind_day(in_doc_remind);
				int j=0;
				List<String> v_doc_id=vechile.getV_doc_id();
				List<String> items = Arrays.asList(new_file_name.split("\\s*,\\s*"));
				
				if(insurance_document_name!=null){
				for(String a : insurance_document_name)
				{
					if(!v_doc_id.get(j).equals("0"))
					{
						VehicleDocModel doc=new VehicleDocModel();
						doc.setInsurance_document_name(a);
						doc.setInsurance_document_expiry(insurance_document_expiry.get(j));
						doc.setInsurance_end_date(insurance_end_date.get(j));
						doc.setRemind_day(insurance_remind.get(j));
						/*if(!items.get(j).equals(""))
						{*/
						if(items.get(j).length()>0)
						{
							doc.setInsurance_card_copy(items.get(j));
						}
						else
						{
							doc.setInsurance_card_copy("");
						}
							
						/*}*/
						/*else
						{
							doc.setInsurance_card_copy(items.get(j));
						}*/
						
						vechileservice.editVechileDocById(Integer.parseInt(v_doc_id.get(j)), doc);
						 
						 
						 
					}
					else
					{
						VehicleDocModel doc=new VehicleDocModel();
						doc.setInsurance_document_name(a);
						doc.setInsurance_document_expiry(insurance_document_expiry.get(j));
						doc.setRemind_day(insurance_remind.get(j));
						doc.setV_id((int) vehicle_id);
						doc.setInsurance_end_date(insurance_end_date.get(j));
						doc.setSchool_id((Integer) session.getAttribute("new_school_id"));
						doc.setInsurance_card_copy(items.get(j));
						doc.setStatus(0);
						doc.setNoti_type(0);
						if(insurance_document_expiry.get(j)!="" && !insurance_document_expiry.get(j).equals(""))
						{
							vechileservice.addVechileDoc(doc);
						}
						
					 
						
					}
					j++;
				}
				}
				vechileservice.editVechileById(vehicle_id,vehiclemodel);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:manageVehicle");
		}
		return new ModelAndView("redirect:manageVehicle");
	}

	/**
	 * Function for delete vehicle by ID
	 **/
	@RequestMapping(value = "admin/deleteVechile", method = RequestMethod.GET)
	public ModelAndView deleteVechile(
			@ModelAttribute("command") VechileBean vechiles,
			BindingResult result) {
		
		vechileservice.deleteVechile(vechiles.getVechile_id());
		return new ModelAndView("redirect:manageVehicle");
	}

	/**
	 * Function for edit school by school id
	 **/
	@RequestMapping(value = "admin/editSchool", method = RequestMethod.GET)
	public ModelAndView schoolInfomation(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		//int school_id=schoolbean.getS_id();
		try{
		String school_id_str=request.getParameter("sc_id");
		byte[] decodedBytes = Base64.decodeBase64(""+school_id_str+"");
		System.out.println(new String(decodedBytes));
		int school_id=Integer.parseInt(new String(decodedBytes));
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		model.put("countries", ControllerHelper. prepareListofBean(schoolservice.listCountry()));
		SchoolModel schoolinfo = (SchoolModel) schoolservice
				.getSchoolById(school_id);
		LoginModel school_admin = schoolservice.getMainSchoolAdmin(school_id);
		/*
		 * if(schoolinfo.getCountry()!=null & schoolinfo.getCountry()!=0 ) {
		 * model.addAttribute("yes", 1); model.addAttribute("country",
		 * schoolinfo.getCountry()); model.addAttribute("city",
		 * schoolinfo.getCity());
		 * 
		 * }else { model.addAttribute("yes", 0); model.addAttribute("country",
		 * ""); model.addAttribute("city", ""); }
		 */
		
		model.addAttribute("schoolModel", schoolinfo);
		model.addAttribute("schooladmin", school_admin);
		model.put("all_school_admin", ControllerHelper. getParentList(schoolservice
				.listSchoolAdmin(school_id) , schoolservice));
		}catch(Exception e)
		{
			System.out.println(e);
		}
		model.addAttribute("heading", "School Information");
		return new ModelAndView("SuperAdmin/school_infomation");
	}

	/**
	 * Function for Update School Information by School id
	 **/
	@RequestMapping(value = "admin/editSchool", method = RequestMethod.POST)
	public ModelAndView schoolInfomation(
			@ModelAttribute("command") SchoolBean school,
			LoginBean schooladmin, BindingResult result, ModelMap model,
			HttpServletRequest request) {
		
		String school_id_str=request.getParameter("sc_id");
		System.out.println("ABC"+school_id_str);
		byte[] decodedBytes = Base64.decodeBase64(""+school_id_str+"");
		System.out.println(new String(decodedBytes));
		int school_id=Integer.parseInt(new String(decodedBytes));
		try {
			
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			String imageName = "";
			if (school.getSchool_logo() != "") {
				String filePath = school.getSchool_logo();
				String[] parts = filePath.split("base64,");
				String part2 = parts[1]; // 034556
				int rand = RandomUtils.nextInt();
				imageName = school.getSchool_name() + rand + ".png";
				String directory =  Assets.SCHOOL_UPLOAD_PATH_SYS + imageName;
				byte[] imageByteArray = Base64.decodeBase64(part2);
				System.out.println(directory);
				FileOutputStream imageOutFile = new FileOutputStream(directory);
				imageOutFile.write(imageByteArray);
				imageOutFile.close();
				
				
			} else {
				imageName = "";

				System.out.println(school.getSchool_logo());
			}

			SchoolModel schoolmodel = new SchoolModel();
			schoolmodel.setCity(0);
			schoolmodel.setCountry(0);
			// schoolmodel.setPrincipal_contact(school.getPrincipal_contact());
			// schoolmodel.setPrincipal_name(school.getPrincipal_name());
			schoolmodel.setSchool_name(school.getSchool_name());
			schoolmodel.setZip_code(0);
			schoolmodel.setTotal_bus(school.getTotal_bus());
			schoolmodel.setTotal_students(school.getTotal_students());
			schoolmodel.setSchool_logo(imageName);
			schoolmodel.setSchool_address(school.getSchool_address());
			schoolmodel.setSchool_lat(school.getSchool_lat());
			schoolmodel.setSchool_lng(school.getSchool_lng());
			schoolmodel.setCountry(school.getCountry());
			schoolmodel.setCity(school.getCity());
			schoolmodel.setSchool_address_field(school.getSchool_address_field());
			schoolservice.editSchoolByAdmin(school_id, schoolmodel);

			LoginModel schooladminmodel1 = new LoginModel();
			schoolservice.editschooladminBySchool(school_id,
					schooladminmodel1);
			/* Function for edit main school admin details */
			LoginModel schooladminmodel = new LoginModel();
			schooladminmodel.setFirst_name(schooladmin.getFirst_name());
			schooladminmodel.setLast_name(schooladmin.getLast_name());
			// schooladminmodel.setContact_number(schooladmin.getContact_number());
			schooladminmodel.setUser_email(schooladmin.getUser_email());
			schooladminmodel.setPermission(schooladmin.getPermission());
			schooladminmodel.setUser_pass("");
			schoolservice.updateMainSchoolAdmin(schooladmin.getUser_id());
			schoolservice.editschooladmin(schooladmin.getUser_id(),
					schooladminmodel);
			Sms_api sms = new Sms_api();
			sms.sendMsg("Your school infomation updated successfully ", 
					schooladmin.getContact_number());
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e);
		}
		model.addAttribute("success", "School information updated successfully");

		model.put("countries",ControllerHelper. prepareListofBean(schoolservice.listCountry()));
		SchoolModel schoolinfo = (SchoolModel) schoolservice
				.getSchoolById(school_id);
		model.addAttribute("schoolModel", schoolinfo);

		LoginModel school_admin = schoolservice.getMainSchoolAdmin(school_id);
		model.addAttribute("schooladmin", school_admin);
		model.put("all_school_admin",
				ControllerHelper.getParentList(schoolservice.listSchoolAdmin(school_id),schoolservice));
		model.addAttribute("heading", "School Information");
		return new ModelAndView("SuperAdmin/school_infomation");
	}

	/**
	 * Function for delete school by school id
	 **/
	@RequestMapping(value = "admin/deleteSchool", method = RequestMethod.GET)
	public ModelAndView deleteSchool(
			@ModelAttribute("command") SchoolBean school, BindingResult result,
			ModelMap modelH) {
		try {
			schoolservice.deleteSchool(school.getS_id());
		} catch (Exception e) {
			System.out.println(e);
		}
		return new ModelAndView("redirect:manageSchools");
	}

	/**
	 * Function for add route
	 **/
	@RequestMapping(value = "admin/addRoute", method = RequestMethod.GET)
	public ModelAndView addRoute(
			@ModelAttribute("command") DriverBean driverbean,
			BindingResult result, HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("heading", "Bus Route");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		SchoolModel school = schoolservice.getSchoolById(school_id);
		
		model.put("school_details", school);

		RouteBean route = new RouteBean() ;
		route.setSource_lat(school.getSchool_lat());
		route.setSource_lng(school.getSchool_lng());
		route.setDestination_lat(school.getSchool_lat());
		route.setDestination_lng(school.getSchool_lng());
		model.put("route",  route)  ;
		
		return new ModelAndView("SuperAdmin/add_route_details", model);
	}

	/**
	 * Function for enter to school
	 **/
	@RequestMapping(value = "admin/enterToSchool", method = RequestMethod.GET)
	public ModelAndView enterToSchool(
			@ModelAttribute("command") LoginModel loginmodel,
			BindingResult result, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		session.setAttribute("school_login", 1);
		// int sess_check=session.getAttribute("school_login");
		// System.out.println();
		// sess_check=(Integer) session.getAttribute("school_login");
		String redirectStr = "";
		if (session.getAttribute("school_login") == null) {
			redirectStr = "SuperAdmin/second_login";

		} else {
			redirectStr = "redirect:/schoolSelect";
		}
		return new ModelAndView(redirectStr);

	}

	@RequestMapping(value = "admin/enterToSchool", method = RequestMethod.POST)
	public ModelAndView enterToSchool(
			@ModelAttribute("command") LoginModel loginmodel,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			if (result.hasErrors()) {
				System.out.println("Some error occrrued");
			} else {
				LoginModel found = loginservice.login(
						loginmodel.getUser_email(), loginmodel.getUser_pass());
				if (found != null
						&& found.getUser_pass().equals(
								loginmodel.getUser_pass())) {
					session.setAttribute("school_login", 1);
					if (found.getUser_role() == 1) {
						return new ModelAndView("redirect:/schoolSelect");
					}
				} else {
					model.addAttribute("error",
							"Please enter correct email and password");
				}

			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return new ModelAndView("SuperAdmin/second_login");
	}

	/**
	 * function for select school for as a work school admin
	 **/
	@RequestMapping(value = "/schoolSelect", method = RequestMethod.GET)
	public ModelAndView schoolSelect(
			@ModelAttribute("command") SchoolBean school, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		model.put("schools", ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		return new ModelAndView("SuperAdmin/select_school");
	}

	/**
	 * function for select school for as a work school admin
	 **/
	@RequestMapping(value = "/selectSchool", method = RequestMethod.POST)
	public ModelAndView selectSchool(
			@ModelAttribute("command") SchoolBean school, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		String url = "";
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		if (school.getS_id() == null) {
			url = "SuperAdmin/select_school";
			model.put("error", "Please select school");
			model.put("schools",
					ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
		} else {
			url = "redirect:admin/schoolDashboard";
		}
		try {
			session.setAttribute("new_school_id", school.getS_id());
		} catch (Exception e) {
			System.out.println(e);
		}

		return new ModelAndView(url);
	}

	/**
	 * function for school dashboard
	 **/
	@RequestMapping(value = "admin/schoolDashboard", method = RequestMethod.GET)
	public ModelAndView schoolDashboard(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try{
			
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			  {
				 return new ModelAndView("redirect:/login.html");
			  }
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			int total_schools = schoolservice.listSchoolAdmin(school_id).size();
			int total_students = schoolservice.listStudent(school_id).size();
			int total_buses = vechileservice.getVechile(school_id).size();
			model.put("total_student", total_students);
			model.put("total_schools", total_schools);
			model.put("total_buses", total_buses);
			model.put("Heading_box", "Total School Admin");
			
		}catch(Exception e)
		{
			System.out.println(e);
			
		}
		return new ModelAndView("SuperAdmin/schoolDashboard");
	}

	/**
	 * Function for add new route
	 **/
	@RequestMapping(value = "admin/addRoute", method = RequestMethod.POST)
	public ModelAndView addRoute(@ModelAttribute("command") RouteBean route,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			RouteModel routemodel = new RouteModel();
           
			System.out.println("Source_: " +  route.getSource_lat() +" --- " +  route.getSource_lng());
			System.out.println("Destination: " +  route.getDestination_lat() +" --- " + route.getDestination_lng());

			routemodel.setDestination_lat( route.getDestination_lat() );
			routemodel.setDestination_lng( route.getDestination_lng() );
			routemodel.setSource_lat( route.getSource_lat() );
			routemodel.setSource_lng( route.getSource_lng() );
			
			routemodel.setSource(route.getSource());
			routemodel.setDestination(route.getDestination());
			routemodel.setRoute_id(null);
			routemodel.setRoute_name(route.getRoute_name());
			routemodel.setSchool_id(school_id);
			routemodel.setNote(route.getNote());
			routemodel.setSource_note(route.getSource_note());
			routemodel.setDestination_note(route.getDestination_note());
			routemodel.setRadius("");
			long route_id = schoolservice.addRoute(routemodel);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:allRoute");
	}



	/**
	 * Method for get all route by school
	 **/
	@RequestMapping(value = "admin/allRoute", method = RequestMethod.GET)
	public ModelAndView allRoute(@ModelAttribute("command") RouteBean route,
			BindingResult result, HttpServletRequest request, ModelMap modelH) {
		
		
			
		try{
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			
			Map<String, Object> model = new HashMap<String, Object>();
			 
		int school_id = (Integer) session.getAttribute("new_school_id");
		modelH.addAttribute("heading", "Manage Route");
		model.put("staffs", ControllerHelper.getAllRoute(schoolservice.listRoute(school_id)));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/all_routes", model);
		}catch(Exception e)
		{
			return new ModelAndView("redirect:/login.html");
		}
		
	}

	

	/**
	 * Function for View Route
	 **/
	@RequestMapping(value = "admin/viewRoute", method = RequestMethod.GET)
	public ModelAndView viewRoute(@ModelAttribute("command") RouteBean route,
			BindingResult result, HttpServletRequest request) {
		
		
		RouteModel routemodel =new RouteModel();
		try{
			String route_id_str=request.getParameter("route_id");
			byte[] decodedBytes = Base64.decodeBase64(""+route_id_str+"");
			int route_id=Integer.parseInt(new String(decodedBytes));
			try{
				routemodel = schoolservice.getRouteById(route_id);
			}catch (NumberFormatException e) {
				System.out.println(e);
			}
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("school_details", schoolservice.getSchoolById(school_id));
		
		if (routemodel != null) {

			model.put("latlong",ControllerHelper.getAllLatLng(schoolservice.listLatLng(route_id), studentservice));
			model.put("heading", "View Route");
			model.put("route", routemodel);
		
			return new ModelAndView("SuperAdmin/view_route_map", model);
		} else {
			return new ModelAndView("redirect:allRoute");
		}
		}catch(NumberFormatException e)
		{
			return new ModelAndView("redirect:allRoute");
			 
		}
	
		

	}

	
	/**
	 * Function for edit Route
	 **/
	@RequestMapping(value = "admin/editRoute", method = RequestMethod.POST)
	public ModelAndView editRoutepost(
			@ModelAttribute("command") RouteBean route, RouteLatLng latlng,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		
		try {
			
		     String route_id_str=request.getParameter("id");
		     byte[] decodedBytes = Base64.decodeBase64(""+route_id_str+"");
		     int route_id=Integer.parseInt(new String(decodedBytes));
				int school_id = (Integer) request.getSession() .getAttribute("new_school_id");

			
			RouteModel routemodel = new RouteModel();

			routemodel.setDestination_lat( route.getDestination_lat() );
			routemodel.setDestination_lng( route.getDestination_lng() );
			routemodel.setSource_lat( route.getSource_lat() );
			routemodel.setSource_lng( route.getSource_lng() );

			routemodel.setSource(route.getSource());
			routemodel.setDestination(route.getDestination());
			routemodel.setRoute_name(route.getRoute_name());
			routemodel.setSchool_id(school_id);
			routemodel.setNote(route.getNote());
			routemodel.setSource_note(route.getSource_note());
			routemodel.setDestination_note(route.getDestination_note());
			
			schoolservice.editRouteById(route_id, routemodel);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return new ModelAndView("redirect:allRoute");


	}

	/**
	 * Function for edit Route
	 **/
	@RequestMapping(value = "admin/editRoute", method = RequestMethod.GET)
	public ModelAndView editRoute(@ModelAttribute("command") RouteBean route,
			BindingResult result, HttpServletRequest request) {
		RouteModel routemodel=new RouteModel();
		try{
			
		String route_id_str=request.getParameter("id");
		byte[] decodedBytes = Base64.decodeBase64(""+route_id_str+"");
		int route_id=Integer.parseInt(new String(decodedBytes));	
		
		try{
		routemodel = schoolservice.getRouteById(route_id);
		}catch(NumberFormatException e)
		{
			System.out.println(e);
		}
		Map<String, Object> model = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		if (routemodel != null) {
			model.put("latlong",
					ControllerHelper.getAllLatLng(schoolservice.listLatLng(route_id), studentservice));
			model.put("heading", "Edit Route");
			model.put("route", routemodel);
			model.put("start_lat",  routemodel.getSource_lat()) ;
			model.put("start_lang", routemodel.getSource_lng()) ;
			model.put("end_lat", routemodel.getDestination_lat()) ;
			model.put("end_lang", routemodel.getDestination_lng()) ;
			return new ModelAndView("SuperAdmin/edit_route", model);
		} else {
			return new ModelAndView("redirect:allRoute");
		}
		}catch(Exception e)
		{	e.printStackTrace();
			return new ModelAndView("redirect:allRoute");
		}

	}

	/**
	 * Function for delete route
	 **/
	@RequestMapping(value = "admin/deleteRoute", method = RequestMethod.GET)
	public ModelAndView deleteRoute(@ModelAttribute("command") RouteBean route,
			BindingResult result, ModelMap modelH) {
		schoolservice.deleteRoute(route.getRoute_id());
		return new ModelAndView("redirect:allRoute");
	}

	/**
	 * Function for assign student route
	 **/
	@RequestMapping(value = "admin/studentRoute", method = RequestMethod.GET)
	public ModelAndView studentRoute(
			@ModelAttribute("command") RouteBean route, BindingResult result,
			HttpServletRequest request, ModelMap modelH) {
		HttpSession session = request.getSession();
		modelH.addAttribute("heading", "Assign Route");
		Map<String, Object> model = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("routes", ControllerHelper.getAllRoute(schoolservice.listRoute(school_id)));
		model.put("students",
				ControllerHelper. getStudentList(schoolservice.listStudent(school_id) ,schoolservice));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/assign_student_route", model);
	}

	/**
	 * Function for assign student route using post method
	 **/
	@RequestMapping(value = "admin/studentRoute", method = RequestMethod.POST)
	public ModelAndView studentRoutePost(
			@ModelAttribute("command") RouteLatLng route, BindingResult result,
			HttpServletRequest request, ModelMap modelH) {

		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		System.out.println("here");
		modelH.addAttribute("heading", "Assign Route");
		Map<String, Object> model = new HashMap<String, Object>();

		try {
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			RouteLatLngModel routelatlng = schoolservice.checkStudentRouteId(
					route.getStudent_id(), school_id);
			if (routelatlng == null) {
				RouteLatLngModel routeNew = new RouteLatLngModel();
				routeNew.setAddress(route.getAddress());
				GoogleResponse res = ControllerHelper.convertToLatLong(route.getAddress());
				if (res.getStatus().equals("OK")) {
					for (Result result1 : res.getResults()) {
						routeNew.setLat(result1.getGeometry().getLocation()
								.getLat());
						routeNew.setLng(result1.getGeometry().getLocation()
								.getLng());
					}
				}
				routeNew.setSchool_id(school_id);
				routeNew.setStudent_id(route.getStudent_id());
				routeNew.setRoute_id(route.getRoute_id());
				routeNew.setId(null);
				routeNew.setParent_id(route.getParent_id());
				routeNew.setStudent_name(route.getStudent_name());
				schoolservice.addAssignRoute(routeNew);
				model.put("success", "Route Assigned successfully");

			} else {

				model.put("error", "You already assigned this student");
			}
			model.put("routes", ControllerHelper.getAllRoute(schoolservice.listRoute(school_id)));
			model.put("students",
					ControllerHelper. getStudentList(schoolservice.listStudent(school_id) ,schoolservice));

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return new ModelAndView("SuperAdmin/assign_student_route", model);
	}

	/**
	 * Function for add School Admin
	 **/
	@RequestMapping(value = "admin/addSchoolAdmin", method = RequestMethod.GET)
	public ModelAndView addSchoolAdmin(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		model.addAttribute("heading", "Add School Admin");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("school_admin",
				ControllerHelper. getParentList(schoolservice.listSchoolAdmin(school_id),schoolservice));
		return new ModelAndView("SuperAdmin/add_school_admin");
	}

	/**
	 * Function for add School Admin Post Data
	 **/
	@RequestMapping(value = "admin/addSchoolAdmin", method = RequestMethod.POST)
	public ModelAndView addSchoolAdmin(
			@ModelAttribute("command") LoginBean schooladmin,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			model.addAttribute("heading", "Add School Admin");
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			/*
			 * LoginModel found =
			 * loginservice.login(schooladmin.getUser_email(), "");
			 */
			LoginModel found = loginservice.checkMobile(schooladmin
					.getContact_number());
			if (found == null) {
				RandomNumber random = new RandomNumber(9999);
				String f_name = schooladmin.getFirst_name().substring(0, 1);
				String password = random.nextSessionId();
				String username = f_name + "_" + schooladmin.getLast_name();
				String username1 = f_name + "_" + schooladmin.getLast_name();
				username = username.toLowerCase();
				username1 = username1.toLowerCase();
				username = username.replaceAll(" ", "_").toLowerCase();
				username1 = username1.replaceAll(" ", "_").toLowerCase();	
				int y = 1;
				int x = 1;
				while (y < 2) {
					LoginModel check = loginservice.checkUsername(username);
					if (check == null) {
						username = username;
						break;
					} else {
						y = 1;
						username = username1 + x;
					}
					x++;
				}
				LoginModel schooladminmodel = new LoginModel();
				schooladminmodel.setUser_email(schooladmin.getUser_email());
				schooladminmodel.setUser_name(username);
				schooladminmodel.setUser_pass(password);
				schooladminmodel.setSchool_id((int) school_id);
				schooladminmodel.setContact_number(schooladmin
						.getContact_number());
				schooladminmodel.setUser_role(2);
				schooladminmodel.setP_status(0);
				schooladminmodel.setFirst_name(schooladmin.getFirst_name());
				schooladminmodel.setLast_name(schooladmin.getLast_name());
				schooladminmodel.setPermission(schooladmin.getPermission());
				schooladminmodel.setMain_school_admin(0);
				schooladminmodel.setDevice_token("");
				schooladminmodel.setDevice_id("");
				schooladminmodel.setMorning_before(0);
				schooladminmodel.setEvening_before(0);
				schooladminmodel.setSms_instant_message(1);
				schooladminmodel.setSms_morning_before(1);
				schooladminmodel.setSms_evening_before(1);
				schooladminmodel.setInstant_message(0);
				schooladminmodel.setSms_wrong_route_on(1);
				schooladminmodel.setSound_setting(0);
				schooladminmodel.setChat_sound(0);
				
				schoolservice.addParent(schooladminmodel);
				Sms_api sms = new Sms_api();
				sms.sendMsg("Your login details are username=" + username
						+ " and password=" + password,
						schooladmin.getContact_number());
				model.addAttribute("username", username);
				model.addAttribute("password", password);
				model.addAttribute("success", "School Admin added successfully");

			} else {
				model.addAttribute("schooladmin", schooladmin);
				model.addAttribute("error", "school admin already exist");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.put("schools",ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		HttpSession session = request.getSession();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_admin",
				ControllerHelper. getParentList(schoolservice.listSchoolAdmin(school_id),schoolservice));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/add_school_admin");

	}

	/**
	 * Function for get all manager of current school
	 **/
	@RequestMapping(value = "admin/manageSchoolsAdmin", method = RequestMethod.GET)
	public ModelAndView manageSchoolsAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap modelH, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		modelH.put("school_details", schoolservice.getSchoolById(school_id));
		modelH.addAttribute("heading", "School Admin");
		Map<String, Object> model = new HashMap<String, Object>();
		/* model.put("parents", getParentList(studentservice.getAllParents())); */
		model.put("parents",
				ControllerHelper. getParentList(schoolservice.listSchoolAdmin(school_id),schoolservice));

		return new ModelAndView("SuperAdmin/all_school_admin", model);
	}

	/**
	 * Function for Edit School Admin
	 **/
	@RequestMapping(value = "admin/editSchoolAdmin", method = RequestMethod.GET)
	public ModelAndView editSchoolAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int school_admin_id=Integer.parseInt(new String(decodedBytes));		
		LoginModel parent = schoolservice.getParentById(school_admin_id);
		Map<String, Object> model = new HashMap<String, Object>();
		if (parent != null) {

			model.put("heading", "Edit School Admin");
			model.put("schools",
					ControllerHelper.	prepareListOfSchool(schoolservice.listSchools()));
			model.put("parent", parent);
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			model.put("school_admin",
					ControllerHelper. getParentList(schoolservice.listSchoolAdmin(school_id),schoolservice));
			SchoolModel schol = schoolservice.getSchoolById(school_id);
			model.put("country_details",
					schoolservice.getCountryById(schol.getCountry()));

			return new ModelAndView("SuperAdmin/edit_school_admin", model);
		} else {
			return new ModelAndView("redirect:manageSchoolsAdmin");
		}
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageSchoolsAdmin");
		}

	}

	/**
	 * Function for Edit School Admin
	 **/
	@RequestMapping(value = "admin/editSchoolAdmin", method = RequestMethod.POST)
	public ModelAndView editSchoolAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int school_admin_id=Integer.parseInt(new String(decodedBytes));	
		try {
			
			LoginModel parentModel = new LoginModel();
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			// parentModel.setContact_number(parents.getContact_number());
			parentModel.setUser_email(parents.getUser_email());
			parentModel.setUser_pass(parents.getUser_pass());
			parentModel.setFirst_name(parents.getFirst_name());
			parentModel.setLast_name(parents.getLast_name());
			parentModel.setPermission(parents.getPermission());
			schoolservice.editschooladmin(school_admin_id, parentModel);
			/* return new ModelAndView("redirect:manageSchoolsAdmin"); */
			model.put("school_details", schoolservice.getSchoolById(school_id));
			model.put("success", "School admin updated successfully");
		} catch (Exception e) {
			System.out.println(e);
		}
		LoginModel parent = schoolservice.getParentById(school_admin_id);
		model.put("parent", parent);
		HttpSession session = request.getSession();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_admin",
				ControllerHelper.	getParentList(schoolservice.listSchoolAdmin(school_id),schoolservice));
		return new ModelAndView("SuperAdmin/edit_school_admin", model);
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageSchoolsAdmin");
		}
	}

	/**
	 * Function for view School Admin
	 **/
	@RequestMapping(value = "admin/viewSchoolAdmin", method = RequestMethod.GET)
	public ModelAndView viewSchoolAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int school_admin_id=Integer.parseInt(new String(decodedBytes));		
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		LoginModel parent = schoolservice.getParentById(school_admin_id);
		Map<String, Object> model = new HashMap<String, Object>();
		if (parent != null) {

			model.put("heading", "View School Admin");
			model.put("schools",
					ControllerHelper.	prepareListOfSchool(schoolservice.listSchools()));
			model.put("parent", parent);
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			model.put("school_admin", parent);
			return new ModelAndView("SuperAdmin/view_school_admin", model);
		} else {
			return new ModelAndView("redirect:manageSchoolsAdmin");
		}
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageSchoolsAdmin");
		}

	}

	/**
	 * Function for delete school admin
	 **/
	@RequestMapping(value = "admin/deleteSchoolAdmin", method = RequestMethod.GET)
	public ModelAndView deleteSchoolAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			HttpServletRequest request) {
		// schoolservice.deleteSchoolAdmin(parents.getUser_id());
		schoolservice.deleteSchoolAdminNew(parents.getUser_id());

		return new ModelAndView("redirect:manageSchoolsAdmin");
	}

	/* Method for add new parent corresponding parent status using GET METHOD */
	@RequestMapping(value = "admin/AddNewParent", method = RequestMethod.GET)
	public ModelAndView AddNewParent(
			@ModelAttribute("command") LoginBean loginbean,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		try{
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		String p_status_str=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+p_status_str+"");
		int p_status_new=Integer.parseInt(new String(decodedBytes));
		
		
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		int p_status = p_status_new;
		model.addAttribute("p_status", p_status);
		model.addAttribute("heading", "Add Parent");
		return new ModelAndView("SuperAdmin/add_new_parent");
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageParents");
		}
		
	}

	/* Method for add new parent corresponding parent status using POST METHOD */
	@RequestMapping(value = "admin/AddNewParent", method = RequestMethod.POST)
	public ModelAndView AddNewParent(
			@ModelAttribute("command") LoginBean parent, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			RandomNumber random = new RandomNumber(9999);
			String f_name = parent.getFirst_name().substring(0, 1);
			String password = random.nextSessionId();
			String username = f_name + "." + parent.getMiddle_name();
			String username1 = f_name + "." + parent.getMiddle_name();
			username = username.toLowerCase();
			username1 = username1.toLowerCase();
			username = username.replaceAll(" ", "_").toLowerCase();
			username1 = username1.replaceAll(" ", "_").toLowerCase();	
			int y = 1;
			int x = 1;
			while (y < 2) {
				LoginModel check = loginservice.checkUsername(username);
				if (check == null) {
					username = username;
					break;
				} else {
					y = 1;
					username = username1 + x;
				}
				x++;
			}

			// LoginModel
			// check=loginservice.checkUsername(parent.getUser_name());
			/*
			 * if(check==null) {
			 */
			LoginModel found = null ;

			if (found == null) {
				LoginModel parentModel = new LoginModel();
				parentModel.setUser_email(parent.getUser_email());
				parentModel.setUser_name(username);
				parentModel.setUser_pass(password);
				parentModel.setSchool_id(parent.getSchool_id());
				parentModel.setContact_number(parent.getContact_number());
				parentModel.setUser_role(3);
				parentModel.setP_status(parent.getP_status());
				parentModel.setFirst_name(parent.getFirst_name());
				// parentModel.setLast_name(parent.getLast_name());
				parentModel.setLast_name("");
				parentModel.setFamily_name(parent.getFamily_name());
				parentModel.setMiddle_name(parent.getMiddle_name());
				parentModel.setMobile_number(parent.getMobile_number());
				parentModel.setDevice_token("");
				/*parentModel.setChat_on(0);
				parentModel.setLang(0);
				parentModel.setNoti_on(0);
				parentModel.setChecked_in_on(0);
				parentModel.setChecked_out_on(0);
				parentModel.setSpeed_on(0);
				parentModel.setMax_speed(40);
				parentModel.setWrong_route_on(0);*/
				parentModel.setChat_on(0);
				parentModel.setLang(0);
				parentModel.setNoti_on(0);
				parentModel.setChecked_in_on(0);
				parentModel.setChecked_out_on(0);
				parentModel.setSpeed_on(1);
				parentModel.setMax_speed(100);
				parentModel.setWrong_route_on(1);
				parentModel.setSms_checked_in_on(1);
				parentModel.setSms_checked_out_on(1);
				parentModel.setSms_speed_on(1);
				parentModel.setSms_max_speed(100);
				parentModel.setSms_wrong_route_on(1);
				parentModel.setInstant_message(0);
				parentModel.setMorning_before(0);
				parentModel.setEvening_before(0);
				parentModel.setSms_instant_message(1);
				parentModel.setSms_morning_before(1);
				parentModel.setSms_evening_before(1);
				parentModel.setDevice_id("");
				parentModel.setSound_setting(0);
				parentModel.setChat_sound(0);
				schoolservice.addParent(parentModel);
				model.addAttribute("success", "Parent Added Successfully");
				model.addAttribute("username", username);
				model.addAttribute("password", password);
				model.addAttribute("heading", "Add Parent");
			} else {
				model.addAttribute("heading", "Add Parent");
				model.addAttribute("parentBean", parent);
				model.addAttribute("error", "This parent already exist");
			}

			/*
			 * RandomNumber random=new RandomNumber(9999); String f_name =
			 * parent.getFirst_name().substring(0, 3); String l_name=
			 * parent.getLast_name().substring(0, 3); String con_number=
			 * parent.getContact_number().substring(0, 3);
			 * 
			 * String username=f_name+random.generateNewRandom(50)+l_name;
			 * LoginModel
			 * check=loginservice.checkUsername(parent.getUser_name());
			 * if(check==null) {
			 * 
			 * LoginModel found = loginservice.login(parent.getUser_email(),"");
			 * if(found==null) {
			 * 
			 * LoginModel parentModel = new LoginModel();
			 * 
			 * 
			 * 
			 * System.out.println(username);
			 * parentModel.setP_status(parent.getP_status());
			 * parentModel.setFirst_name(parent.getFirst_name());
			 * parentModel.setLast_name(parent.getLast_name());
			 * parentModel.setUser_email(parent.getUser_email());
			 * parentModel.setUser_name(username);
			 * parentModel.setUser_pass(random.nextSessionId());
			 * parentModel.setSchool_id(parent.getSchool_id());
			 * parentModel.setContact_number(parent.getContact_number());
			 * parentModel.setUser_role(3);
			 * schoolservice.addParent(parentModel);
			 * model.addAttribute("success", "Parent Added Successfully");
			 * model.addAttribute("heading", "Add Parent"); }else {
			 * model.addAttribute("heading", "Add Parent");
			 * model.addAttribute("parentBean", parent);
			 * model.addAttribute("error", "This parent already exist"); } }else
			 * { model.addAttribute("heading", "Add Parent");
			 * model.addAttribute("parentBean", parent);
			 * model.addAttribute("error", "This user already exist"); }
			 */
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpSession session = request.getSession();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/add_parent");

	}

	/**
	 * Function for view Student
	 **/
	@RequestMapping(value = "admin/viewAttendance", method = RequestMethod.GET)
	public ModelAndView viewAttendance(
			@ModelAttribute("command") StudentBean students,
			BindingResult result, HttpServletRequest request) {
		StudentModel studentModel = new StudentModel();
		try {
		
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");	
		int student_id=Integer.parseInt(new String(decodedBytes));
		studentModel = studentservice.getStudentById(student_id);

		Map<String, Object> model = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");

		if (studentModel != null) {
			try {

			
			model.put("heading", "View Attendance");
			model.put("student", studentModel);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			model.put("current_date", dateFormat.format(date));
			model.put("routes", ControllerHelper.getAllRoute(schoolservice
					.listRoute(student_id)));
			RouteLatLngModel latlngmodel = studentservice
					.getLatLngBySId(student_id);
					model.put("allParent",ControllerHelper. getParentList(schoolservice
					.getParentByPIdList(studentModel.getP_status_id()),schoolservice));
			model.put("latlng", latlngmodel);
			
			//Get holiday by school id 
			List<HolidayBean> a=ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id));
			List<HolidayDeletedBean> b=ControllerHelper.getDeletedHolidayList(schoolservice.getAllDeletedHoliday(school_id));
			if(a!=null)
			{
				
			
				
			for(int i=0;i<a.size();i++)
			{
				if(b!=null)
				{
					for(int j=0;j<b.size();j++)
					{
						if(a.get(i).getH_id().equals(b.get(j).getHoliday_id()))
						{
							a.remove(i);
						}
					}
				}else{
					
				}
				
			}
			model.put("all_holiday",a);
			}else{
				model.put("all_holiday","");
			}
			
			
			//model.put("all_holiday",ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id)));
			model.put("all_present", ControllerHelper.getPresentList(schoolservice
			.getStudentAttendanceList(student_id)));

			} catch (Exception e) {
				e.printStackTrace();
				return new ModelAndView("redirect:manageStudents");
			}
			model.put("student_id", student_id);
			model.put("school_details", schoolservice.getSchoolById(school_id));
			Calendar now = Calendar.getInstance();
			
			int iMonth=0;
			int year_num=now.get(Calendar.YEAR);
			
			String month=""+(now.get(Calendar.MONTH) + 1);
			
			System.out.println("--month"+month);
			if((now.get(Calendar.MONTH) + 1)==01)
			{
				month="01";
				iMonth =Calendar.JANUARY;
			}else if ((now.get(Calendar.MONTH) + 1)==02) {
				month="02";
				iMonth =Calendar.FEBRUARY;
			}else if ((now.get(Calendar.MONTH) + 1)==03) {
				month="03";
				iMonth =Calendar.MARCH;
			}else if ((now.get(Calendar.MONTH) + 1)==04) {
				month="04";
				iMonth =Calendar.APRIL;
			}else if ((now.get(Calendar.MONTH) + 1)==05) {
				month="05";
				iMonth =Calendar.MAY;
			}else if ((now.get(Calendar.MONTH) + 1)==06) {
				month="06";
				iMonth =Calendar.JUNE;
			}else if ((now.get(Calendar.MONTH) + 1)==07) {
				month="07";
				iMonth =Calendar.JULY;
			}else if ((now.get(Calendar.MONTH) + 1)==8) {
				month="08";
				iMonth =Calendar.AUGUST;
			}else if ((now.get(Calendar.MONTH) + 1)==9) {
				month="09";
				iMonth =Calendar.SEPTEMBER;
			}else if ((now.get(Calendar.MONTH) + 1)==10) {
				month="10";
				iMonth =Calendar.OCTOBER;
			}else if ((now.get(Calendar.MONTH) + 1)==11) {
				month="11";
				iMonth =Calendar.NOVEMBER;
			}else {
				month="12";
				iMonth =Calendar.DECEMBER;
			}
			
			String year=""+year_num;
			int total_present=0;
			int absent_day=0;
			total_present=schoolservice.getStudentAttendanceListByMonth(student_id,month,year).size();
			int iYear = Integer.parseInt(year.trim());
			int iDay = 1;
			Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);
			int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
			System.out.println("Day in month="+daysInMonth);
			List<HolidayBean> b=ControllerHelper.getHolidayList(schoolservice.getAllHolidayByMonth(school_id,month,year));
			List<HolidayDeletedBean> c=ControllerHelper.getDeletedHolidayList(schoolservice.getAllDeletedHoliday(school_id));
			try{
			for(int i=0;i<b.size();i++)
			{
				for(int j=0;j<c.size();j++)
				{
					if(b.get(i).getH_id().equals(c.get(j).getHoliday_id()))
					{
						b.remove(i);
					}
				}
			}
			}catch(Exception e)
			{
				System.out.println(e);
			}
			
			if(b!=null)
			{
				absent_day=absent_day- b.size();
				model.put("holiday_days", b.size());
			}else
			{
				model.put("holiday_days",0);
			}
			absent_day=daysInMonth-total_present;
			model.put("present_day", total_present);
			model.put("absent_day", absent_day);
			
			
			return new ModelAndView("SuperAdmin/view_student_attendance", model);
		} else {
			return new ModelAndView("redirect:manageStudents");
		}
		} catch (Exception e) {
			return new ModelAndView("redirect:manageStudents");
		}

	}

	

	

	@RequestMapping(value = "admin/addHoliday", method = RequestMethod.GET)
	public ModelAndView addHoliday(
			@ModelAttribute("command") HolidayBean holidaybean,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		model.addAttribute("heading", "Add Holiday");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/add_holiday");
	}

	@RequestMapping(value = "admin/addHoliday", method = RequestMethod.POST)
	public ModelAndView addHoliday(
			@ModelAttribute("command") HolidayBean holidaybean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			
			
			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-mm-dd");
			String inputString1 = holidaybean.getHoliday_date();
			String inputString2 =holidaybean.getHoliday_enddate();
			Date date1 = myFormat.parse(inputString1);
			Date date2=new Date();
			if(!inputString2.equals(""))
			{
				date2 = myFormat.parse(inputString2);
				
			}else
			{
				date2 = myFormat.parse(inputString1);
			}
			
			long diff = date2.getTime() - date1.getTime();
			diff=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			Calendar cal = Calendar.getInstance();
			//adding one day to current date 
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
			for(int i=0;i<=diff;i++)
			{
				Date date = formatter.parse(holidaybean.getHoliday_date());
				cal.setTime(formatter.parse(holidaybean.getHoliday_date()));

				cal.add(Calendar.DATE, i);
				Date newDate = cal.getTime();
				
				HolidayModel holidaymodel = new HolidayModel();
				holidaymodel.setH_id(null);
				holidaymodel.setHoliday_date(formatter.format(newDate));
				holidaymodel.setHoliday_name(holidaybean.getHoliday_name());
				if(!holidaybean.getHoliday_enddate().equals(""))
				{
					holidaymodel.setHoliday_enddate(holidaybean.getHoliday_enddate());
				}else
				{
					holidaymodel.setHoliday_enddate(holidaybean.getHoliday_date());
				}
				
				holidaymodel.setSchool_id(holidaybean.getSchool_id());
				holidaymodel.setHoliday_start_date(holidaybean.getHoliday_date());
				schoolservice.addHoliday(holidaymodel);
			}	
			
			model.addAttribute("success", "Holiday Added Successfully");
			model.addAttribute("heading", "Add Holiday");

		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpSession session = request.getSession();

		int school_id = (Integer) session.getAttribute("new_school_id");
		model.addAttribute("heading", "View Holiday Calendar");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy");
		DateFormat dateFormat2 = new SimpleDateFormat("MM");

		Date date = new Date();
		int year = Integer.parseInt(dateFormat1.format(date));
		int month = Integer.parseInt(dateFormat2.format(date));
		model.put("school_id", school_id);
		model.put("current_date", dateFormat.format(date));
		model.put("all_holiday",
				ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id)));
		model.put("holidays", ControllerHelper.getHolidays(frontService.getHolidayByMonth(month,
				year, school_id)));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/view_admin_holiday_calendar", model);

		/*
		 * int school_id=(Integer) session.getAttribute("new_school_id");
		 * model.put("school_details", schoolservice.getSchoolById(school_id));
		 * return new ModelAndView("SuperAdmin/add_holiday");
		 */

	}

	/**
	 * Function for add Super Admin
	 **/
	@RequestMapping(value = "admin/addSuperAdmin", method = RequestMethod.GET)
	public ModelAndView addSuperAdmin(
			@ModelAttribute("command") SchoolBean schoolbean,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		model.addAttribute("heading", "Add Super Admin");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		model.put("school_admin",ControllerHelper.getSuperList(schoolservice.listSuperAdmin()));
		return new ModelAndView("SuperAdmin/add_super_admin");
	}

	/**
	 * Function for add School Admin Post Data
	 **/
	@RequestMapping(value = "admin/addSuperAdmin", method = RequestMethod.POST)
	public ModelAndView addSuperAdmin(
			@ModelAttribute("command") LoginBean schooladmin,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			model.addAttribute("heading", "Add School Admin");
			LoginModel found = loginservice.checkMobile(schooladmin
					.getContact_number());
			if (found == null) {
				RandomNumber random = new RandomNumber(9999);
				String f_name = schooladmin.getFirst_name().substring(0, 1);
				String password = random.nextSessionId();
				String username = f_name + "_" + schooladmin.getLast_name();
				String username1 = f_name + "_" + schooladmin.getLast_name();
				username = username.toLowerCase();
				username1 = username1.toLowerCase();
				username = username.replaceAll(" ", "_").toLowerCase();
				username1 = username1.replaceAll(" ", "_").toLowerCase();	
				int y = 1;
				int x = 1;
				while (y < 2) {
					LoginModel check = loginservice.checkUsername(username);
					if (check == null) {
						username = username;
						break;
					} else {
						y = 1;
						username = username1 + x;
					}
					x++;
				}
				LoginModel schooladminmodel = new LoginModel();
				schooladminmodel.setUser_email(schooladmin.getUser_email());
				schooladminmodel.setUser_name(username);
				schooladminmodel.setUser_pass(password);
				schooladminmodel.setSchool_id(0);
				schooladminmodel.setContact_number(schooladmin
						.getContact_number());
				schooladminmodel.setUser_role(1);
				schooladminmodel.setP_status(0);
				schooladminmodel.setFirst_name(schooladmin.getFirst_name());
				schooladminmodel.setLast_name(schooladmin.getLast_name());
				schooladminmodel.setPermission(schooladmin.getPermission());
				schooladminmodel.setMain_school_admin(0);
				schooladminmodel.setDevice_token("");
				schooladminmodel.setDevice_id("");
				schooladminmodel.setMorning_before(0);
				schooladminmodel.setEvening_before(0);
				schooladminmodel.setSms_instant_message(1);
				schooladminmodel.setSms_morning_before(1);
				schooladminmodel.setSms_evening_before(1);
				schooladminmodel.setInstant_message(0);
				schooladminmodel.setSms_wrong_route_on(1);
				schooladminmodel.setSound_setting(0);
				schooladminmodel.setChat_sound(0);
				schoolservice.addParent(schooladminmodel);
				Sms_api sms = new Sms_api();
				sms.sendMsg("Your login details are username=" + username
						+ " and password=" + password, 
						schooladmin.getContact_number());
				model.addAttribute("username", username);
				model.addAttribute("password", password);
				model.addAttribute("success", "Super Admin added successfully");

			} else {
				model.addAttribute("schooladmin", schooladmin);
				model.addAttribute("error", "Super admin already exist");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.put("schools",ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		model.put("school_admin",ControllerHelper.getSuperList(schoolservice.listSuperAdmin()));
		return new ModelAndView("SuperAdmin/add_super_admin");

	}

	

	/**
	 * Function for get all manager of current school
	 **/
	@RequestMapping(value = "admin/manageSuperAdmin", method = RequestMethod.GET)
	public ModelAndView manageSuperAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap modelH, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		modelH.addAttribute("heading", "Super Admin");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("parents",ControllerHelper.getSuperList(schoolservice.listSuperAdmin()));
		return new ModelAndView("SuperAdmin/all_super_admin", model);
	}

	/**
	 * Function for delete school admin
	 **/
	@RequestMapping(value = "admin/deleteSuperAdmin", method = RequestMethod.GET)
	public ModelAndView deleteSuperAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			HttpServletRequest request) {
		//schoolservice.deleteSchoolAdmin(parents.getUser_id());
		schoolservice.deleteSuperAdmin(parents.getUser_id());
		return new ModelAndView("redirect:manageSuperAdmin");
	}

	/**
	 * Function for Edit School Admin
	 **/
	@RequestMapping(value = "admin/editSuperAdmin", method = RequestMethod.GET)
	public ModelAndView editSuperAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int super_id=Integer.parseInt(new String(decodedBytes));	
		LoginModel parent = schoolservice.getParentById(super_id);
		Map<String, Object> model = new HashMap<String, Object>();
		if (parent != null) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			model.put("heading", "Edit School Admin");
			model.put("schools",
					ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
			model.put("parent", parent);
			model.put("school_admin",
					ControllerHelper.getSuperList(schoolservice.listSuperAdmin()));
			return new ModelAndView("SuperAdmin/edit_super_admin", model);
		} else {
			return new ModelAndView("redirect:manageSuperAdmin");
		}
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageSuperAdmin");
		}

	}

	/**
	 * Function for Edit School Admin
	 **/
	@RequestMapping(value = "admin/editSuperAdmin", method = RequestMethod.POST)
	public ModelAndView editSuperAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int super_id=Integer.parseInt(new String(decodedBytes));
		try{	
			LoginModel parentModel = new LoginModel();
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			// parentModel.setContact_number(parents.getContact_number());
			parentModel.setUser_email(parents.getUser_email());
			parentModel.setUser_pass(parents.getUser_pass());
			parentModel.setFirst_name(parents.getFirst_name());
			parentModel.setLast_name(parents.getLast_name());
			parentModel.setPermission(parents.getPermission());
			schoolservice.editschooladmin(super_id, parentModel);
			/* return new ModelAndView("redirect:manageSchoolsAdmin"); */
			model.put("success", "Super admin updated successfully");
		} catch (Exception e) {
			System.out.println(e);
		}
		LoginModel parent = schoolservice.getParentById(super_id);
		model.put("parent", parent);
		model.put("school_admin",ControllerHelper.getSuperList(schoolservice.listSuperAdmin()));
		return new ModelAndView("SuperAdmin/edit_super_admin", model);
		} catch (Exception e) {
			return new ModelAndView("redirect:manageSuperAdmin");
		}
	}

	/**
	 * Function for view School Admin
	 **/
	@RequestMapping(value = "admin/viewSuperAdmin", method = RequestMethod.GET)
	public ModelAndView viewSuperAdmin(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			HttpServletRequest request) {
		try
		{
		
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int super_id=Integer.parseInt(new String(decodedBytes));	
		LoginModel parent = schoolservice.getParentById(super_id);
		Map<String, Object> model = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		if (parent != null) {

			model.put("heading", "View Super Admin");
			model.put("schools",
					ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
			model.put("parent", parent);
			model.put("school_admin", parent);
			return new ModelAndView("SuperAdmin/view_super_admin", model);
		} else {
			return new ModelAndView("redirect:manageSuperAdmin");
		}
		
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageSuperAdmin");
		}

	}

	/**
	 * Function for get all holiday of current school
	 **/
	@RequestMapping(value = "admin/manageHoliday", method = RequestMethod.GET)
	public ModelAndView manageHoliday(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap modelH, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		modelH.addAttribute("heading", "Holiday");
		Map<String, Object> model = new HashMap<String, Object>();
		
		
		List<HolidayBean> a=ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id));
		List<HolidayDeletedBean> b=ControllerHelper.getDeletedHolidayList(schoolservice.getAllDeletedHoliday(school_id));
		
		try{
		for(int i=0;i<a.size();i++)
		{
			for(int j=0;j<b.size();j++)
			{
				if(a.get(i).getH_id().equals(b.get(j).getHoliday_id()))
				{
					a.remove(i);
				}
			}
		}
		}catch(Exception e)
		{
			System.out.println(e);
		}
		
		
		
		
		model.put("all_holiday",a);
		model.put("deleted_holiday", ControllerHelper.getDeletedHolidayList(schoolservice.getAllDeletedHoliday(school_id)));
		
		
		
		
		/*model.put("all_holiday",
				ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id)));*/
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/all_holiday", model);
	}

	/**
	 * Function for edit parent
	 **/
	@RequestMapping(value = "admin/editHoliday", method = RequestMethod.GET)
	public ModelAndView editHoliday(
			@ModelAttribute("command") HolidayBean holiday,
			BindingResult result, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int holiday_id=Integer.parseInt(new String(decodedBytes));	
		HolidayModel holidaymodel = schoolservice.getHolidayById(holiday_id);
		String loadView = null;
		Map<String, Object> model = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		if (holidaymodel != null) {

			model.put("heading", "Edit Holiday");
			model.put("holiday", holidaymodel);
			model.put("schools",
					ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
			model.put("parent", holidaymodel);
			return new ModelAndView("SuperAdmin/edit_holiday", model);
		} else {
			return new ModelAndView("redirect:manageHoliday");
		}
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageHoliday");
		}

	}

	/**
	 * Function for edit parent
	 **/
	@RequestMapping(value = "admin/editHoliday", method = RequestMethod.POST)
	public ModelAndView editHoliday(
			@ModelAttribute("command") HolidayBean holiday,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int holiday_id=Integer.parseInt(new String(decodedBytes));
		HolidayModel holidaymodel = new HolidayModel();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		holidaymodel.setHoliday_date(holiday.getHoliday_date());
		holidaymodel.setHoliday_name(holiday.getHoliday_name());
		holidaymodel.setHoliday_enddate(holiday.getHoliday_enddate());
		schoolservice.editHolidayById(holiday_id, holidaymodel);
		model.put("school_details", schoolservice.getSchoolById(school_id));
		// return new ModelAndView("redirect:manageParents");
		HolidayModel holidaymodel1 = schoolservice.getHolidayById(holiday_id);
		model.put("heading", "Edit Holiday");
		model.put("schools",ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
		model.put("holiday", holidaymodel1);
		model.put("success", "Holiday updated successfully");
		return new ModelAndView("SuperAdmin/edit_holiday", model);
		}catch(Exception e)
		{
			return new ModelAndView("SuperAdmin/edit_holiday", model);
		}
	}

	@RequestMapping(value = "admin/deleteHoliday", method = RequestMethod.GET)
	public ModelAndView deleteHoliday(
			@ModelAttribute("command") HolidayBean holiday,
			BindingResult result, ModelMap modelH, HttpServletRequest request) {
		System.out.println(holiday.getH_id());
		schoolservice.deleteHoliday(holiday.getH_id());
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("success", "Holiday Deleted deleted successfully");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		if (session.getAttribute("new_school_id") == null) {
			return new ModelAndView("redirect:manageAdminHoliday", model);
		} else {
			return new ModelAndView("redirect:manageHoliday", model);
		}

	}

	
	/**
	 * Function for get all holiday of current school
	 **/
	@RequestMapping(value = "admin/viewCalendar", method = RequestMethod.GET)
	public ModelAndView viewCalendar(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap modelH, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");
		modelH.addAttribute("heading", "View Holiday Calendar");
		Map<String, Object> model = new HashMap<String, Object>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy");
		DateFormat dateFormat2 = new SimpleDateFormat("MM");

		Date date = new Date();
		int year = Integer.parseInt(dateFormat1.format(date));
		int month = Integer.parseInt(dateFormat2.format(date));
		model.put("school_id", school_id);
		model.put("current_date", dateFormat.format(date));
		model.put("all_holiday",
				ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id)));
		model.put("holidays", ControllerHelper.getHolidays(frontService.getHolidayByMonth(month,
				year, school_id)));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/view_admin_holiday_calendar", model);
	}

	
	/**
	 * Function for get all holiday of current school
	 **/
	@RequestMapping(value = "admin/viewAdminCalendar", method = RequestMethod.GET)
	public ModelAndView viewAdminCalendar(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap modelH, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = 0;	
		modelH.addAttribute("heading", "View Holiday Calendar");
		Map<String, Object> model = new HashMap<String, Object>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy");
		DateFormat dateFormat2 = new SimpleDateFormat("MM");

		Date date = new Date();
		int year = Integer.parseInt(dateFormat1.format(date));
		int month = Integer.parseInt(dateFormat2.format(date));
		model.put("school_id", school_id);
		model.put("current_date", dateFormat.format(date));
		model.put("all_holiday", ControllerHelper.getHolidayList(schoolservice.getAllHoliday(0)));
		model.put("holidays",
				ControllerHelper.getHolidays(frontService.getHolidayByMonth(month, year, 0)));
		return new ModelAndView("SuperAdmin/view_admin_holiday_calendar", model);
	}

	/**
	 * Function for get all holiday of current school
	 **/
	@RequestMapping(value = "admin/manageAdminHoliday", method = RequestMethod.GET)
	public ModelAndView manageAdminHoliday(
			@ModelAttribute("command") LoginBean parents, BindingResult result,
			ModelMap modelH, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = 0;
		modelH.addAttribute("heading", "Holiday");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("all_holiday", ControllerHelper.getHolidayList(schoolservice.getAllHoliday(0)));
		model.put("school_details", schoolservice.getSchoolById(0));
		return new ModelAndView("SuperAdmin/all_admin_holiday", model);
	}

	@RequestMapping(value = "admin/addAdminHoliday", method = RequestMethod.GET)
	public ModelAndView addAdminHoliday(
			@ModelAttribute("command") HolidayBean holidaybean,
			BindingResult result, HttpServletRequest request, ModelMap model) {
		model.addAttribute("heading", "Add Holiday");
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = 0;
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("SuperAdmin/add_admin_holiday");
	}

	@RequestMapping(value = "admin/addAdminHoliday", method = RequestMethod.POST)
	public ModelAndView addAdminHoliday(
			@ModelAttribute("command") HolidayBean holidaybean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = 0;
			
			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-mm-dd");
			String inputString1 = holidaybean.getHoliday_date();
			String inputString2 =holidaybean.getHoliday_enddate();
			Date date1 = myFormat.parse(inputString1);
			Date date2 =new Date();
			if(!inputString2.equals(""))
			{
				 date2 = myFormat.parse(inputString2);
			}else
			{
				 date2 = myFormat.parse(inputString1);
			}
			long diff = date2.getTime() - date1.getTime();
			diff=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			Calendar cal = Calendar.getInstance();
			//adding one day to current date 
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
		    for(int i=0;i<=diff;i++)
			{
		    	 Date date = formatter.parse(holidaybean.getHoliday_date());
		    	cal.setTime(formatter.parse(holidaybean.getHoliday_date()));
			 
				cal.add(Calendar.DATE, i);
				Date newDate = cal.getTime();
				HolidayModel holidaymodel = new HolidayModel();
				holidaymodel.setH_id(null);
				holidaymodel.setHoliday_date(formatter.format(newDate));
				holidaymodel.setHoliday_start_date(holidaybean.getHoliday_date());
				holidaymodel.setHoliday_name(holidaybean.getHoliday_name());
				if(!holidaybean.getHoliday_enddate().equals(""))
				{
					holidaymodel.setHoliday_enddate(holidaybean.getHoliday_enddate());
				}else
				{
					holidaymodel.setHoliday_enddate(holidaybean.getHoliday_date());
				}
				
				holidaymodel.setSchool_id(0);
				schoolservice.addHoliday(holidaymodel);
			}
			model.addAttribute("success", "Holiday Added Successfully");
			model.addAttribute("heading", "Add Holiday");

		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpSession session = request.getSession();

		int school_id = 0;
		model.addAttribute("heading", "View Holiday Calendar");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy");
		DateFormat dateFormat2 = new SimpleDateFormat("MM");

		Date date = new Date();
		int year = Integer.parseInt(dateFormat1.format(date));
		int month = Integer.parseInt(dateFormat2.format(date));
		model.put("school_id", school_id);
		model.put("current_date", dateFormat.format(date));
		model.put("all_holiday",
				ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id)));
		model.put("holidays", ControllerHelper.getHolidays(frontService.getHolidayByMonth(month,
				year, school_id)));
		return new ModelAndView("SuperAdmin/view_admin_holiday_calendar", model);

		/*
		 * int school_id=(Integer) session.getAttribute("new_school_id");
		 * model.put("school_details", schoolservice.getSchoolById(school_id));
		 * return new ModelAndView("SuperAdmin/add_holiday");
		 */

	}

	/**
	 * Function for edit parent
	 **/
	@RequestMapping(value = "admin/editAdminHoliday", method = RequestMethod.GET)
	public ModelAndView editAdminHoliday(
			@ModelAttribute("command") HolidayBean holiday,
			BindingResult result, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int holiday_id=Integer.parseInt(new String(decodedBytes));	
		HolidayModel holidaymodel = schoolservice.getHolidayById(holiday_id);
		String loadView = null;
		Map<String, Object> model = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = 0;
		model.put("school_details",
				schoolservice.getSchoolById(holidaymodel.getSchool_id()));
		if (holidaymodel != null) {

			model.put("heading", "Edit Holiday");
			model.put("holiday", holidaymodel);
			model.put("schools",
					ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
			model.put("parent", holidaymodel);
			return new ModelAndView("SuperAdmin/edit_admin_holiday", model);
		} else
			return new ModelAndView("redirect:manageHoliday");
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageHoliday");
		}
	}

	/**
	 * Function for edit parent
	 **/
	@RequestMapping(value = "admin/editAdminHoliday", method = RequestMethod.POST)
	public ModelAndView editAdminHoliday(
			@ModelAttribute("command") HolidayBean holiday,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int holiday_id=Integer.parseInt(new String(decodedBytes));	
		HolidayModel holidaymodel = new HolidayModel();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = holiday.getSchool_id();
		holidaymodel.setSchool_id(holiday.getSchool_id());
		holidaymodel.setHoliday_date(holiday.getHoliday_date());
		holidaymodel.setHoliday_name(holiday.getHoliday_name());
		holidaymodel.setHoliday_enddate(holiday.getHoliday_enddate());
		schoolservice.editHolidayById(holiday_id, holidaymodel);
		// model.put("school_details", schoolservice.getSchoolById(school_id));
		// return new ModelAndView("redirect:manageParents");
		HolidayModel holidaymodel1 = schoolservice.getHolidayById(holiday_id);
		model.put("heading", "Edit Holiday");
		model.put("schools", ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
		model.put("holiday", holidaymodel1);
		model.put("success", "Holiday updated successfully");
		return new ModelAndView("SuperAdmin/edit_admin_holiday", model);
		}catch(Exception e)
		{
			return new ModelAndView("redirect:viewCalendar");
		}
	}

	/**
	 * Function for send password to parent
	 **/
	@RequestMapping(value = "admin/sendParentPassword", method = RequestMethod.POST)
	public ModelAndView sendParentPassword(
			@ModelAttribute("command") LoginBean parent, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int user_id = parent.getUser_id();
		String contact_number = parent.getMobile_number()
				+ parent.getContact_number();
		String str=""+user_id+"";
		byte[] encodedBytes = Base64.encodeBase64(str.getBytes());
		
		String current_url = parent.getUser_pass() + "?parent=" + new String(encodedBytes);
		String message = parent.getSchool_name();
		System.out.println(message);
		Sms_api sms = new Sms_api();
		sms.sendMsg(message, contact_number);
		
		return new ModelAndView("redirect:" + current_url);
	}

	/**
	 * Function for track student by route id
	 **/
	@RequestMapping(value = "admin/TrackStudent", method = RequestMethod.GET)
	public ModelAndView TrackStudent(
			@ModelAttribute("command") StudentBean studentbean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			StudentModel studentModel = new StudentModel();
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int student_id=Integer.parseInt(new String(decodedBytes));
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			try{
			studentModel = studentservice
					.getStudentById(student_id);
			}catch(NumberFormatException e)
			{	e.printStackTrace();
				return new ModelAndView("redirect:manageStudents");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			model.put("students", ControllerHelper.getDriverTrackList(studentservice
					.getDriverTrack(studentModel.getS_route_id()), schoolservice));
			model.put("route_id", studentModel.getS_route_id());
			RouteModel routemodel =new RouteModel();
			System.out.println("Student Route"+studentModel.getS_route_id());
			routemodel = schoolservice.getRouteById(studentModel.getS_route_id());
			if (routemodel != null) {
				model.put("latlong",ControllerHelper.getAllLatLng(schoolservice.listLatLng(studentModel.getS_route_id()), studentservice));
				model.put("route", routemodel);
			}
			model.put("students", ControllerHelper.getDriverTrackList(studentservice
					.getDriverTrack(studentModel.getS_route_id()), schoolservice));
			System.out.println("Route id="+studentModel.getS_route_id());
			model.put("size_l",ControllerHelper.getDriverTrackList(studentservice.getDriverTrack(studentModel.getS_route_id()), schoolservice).size() - 1);
			model.put("first_lat_lng", ControllerHelper.getDriverTrackList(studentservice.getDriverTrackLimit(studentModel.getS_route_id()), schoolservice));
			
			DriverModel DM=new DriverModel();
			DM=schoolservice.getDriverByRouteId(studentModel.getS_route_id());
			
		    DriverAttendanceModel dam=new DriverAttendanceModel();
		    dam=schoolservice.getTodayDriverAttendance(DM.getDriver_id());
			
		  //  model.put("driver_login_date",dam.getLogin_date());
		    // model.put("driver_login_date",dam.getLogin_date());
		    model.put("all_students", ControllerHelper.
		    		getStudentList(schoolservice.getStudentsByRouteId(studentModel.getS_route_id()),schoolservice));
		    if(dam!=null)
			   {
				   model.put("driver_login_date",dam.getLogin_time());
			   }else
			   {
				   model.put("driver_login_date","");
			   }
		    List<VechileBean> v=new ArrayList<VechileBean>();
			v=ControllerHelper.getDriverVehicleList(schoolservice.getVechileByDriverId(DM.getDriver_id()));
			if(v!=null)
			{
				  model.put("vehicle_details",ControllerHelper.getDriverVehicleList(schoolservice.getVechileByDriverId(DM.getDriver_id())));
			}else
			{
				  model.put("vehicle_details","");
			}
			DriverModel drivermodel = schoolservice.getDriverById(DM.getDriver_id());
			model.put("driver_details", drivermodel);
			
			
		} catch (Exception e) {
			
			
			
			StudentModel studentModel = new StudentModel();
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int student_id=Integer.parseInt(new String(decodedBytes));
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			try{
			studentModel = studentservice
					.getStudentById(student_id);
			}catch(NumberFormatException e1)
			{	e1.printStackTrace();
				return new ModelAndView("redirect:manageStudents");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
			model.put("students", ControllerHelper.getDriverTrackList(studentservice
					.getDriverTrack(studentModel.getS_route_id()), schoolservice));
			model.put("route_id", studentModel.getS_route_id());
			RouteModel routemodel =new RouteModel();
			System.out.println("Student Route"+studentModel.getS_route_id());
			routemodel = schoolservice.getRouteById(studentModel.getS_route_id());
			if (routemodel != null) {
				model.put("latlong",ControllerHelper.getAllLatLng(schoolservice.listLatLng(studentModel.getS_route_id()), studentservice));
				model.put("route", routemodel);
			}
			model.put("students", ControllerHelper.getDriverTrackList(studentservice
					.getDriverTrack(studentModel.getS_route_id()), schoolservice));
			System.out.println("Route id="+studentModel.getS_route_id());
			//model.put("size_l",ControllerHelper.getDriverTrackList(studentservice.getDriverTrack(studentModel.getS_route_id())).size() - 1);
			model.put("first_lat_lng", ControllerHelper.getDriverTrackList(studentservice.getDriverTrackLimit(studentModel.getS_route_id()), schoolservice));
			
			DriverModel DM=new DriverModel();
			DM=schoolservice.getDriverByRouteId(studentModel.getS_route_id());
			
		    DriverAttendanceModel dam=new DriverAttendanceModel();
		    dam=schoolservice.getTodayDriverAttendance(DM.getDriver_id());
			
		  //  model.put("driver_login_date",dam.getLogin_date());
		    // model.put("driver_login_date",dam.getLogin_date());
		    model.put("all_students",
		    	ControllerHelper.	getStudentList(schoolservice.getStudentsByRouteId(studentModel.getS_route_id()),schoolservice));
		    if(dam!=null)
			   {
				   model.put("driver_login_date",dam.getLogin_time());
			   }else
			   {
				   model.put("driver_login_date","");
			   }
		    List<VechileBean> v=new ArrayList<VechileBean>();
			v=ControllerHelper.getDriverVehicleList(schoolservice.getVechileByDriverId(DM.getDriver_id()));
			if(v!=null)
			{
				  model.put("vehicle_details",ControllerHelper. getDriverVehicleList(schoolservice.getVechileByDriverId(DM.getDriver_id())));
			}else
			{
				  model.put("vehicle_details","");
			}
			DriverModel drivermodel = schoolservice.getDriverById(DM.getDriver_id());
			model.put("driver_details", drivermodel);
			
			
			model.put("error", "No data available1");
			e.printStackTrace();
			
		}

		return new ModelAndView("SuperAdmin/track_student", model);
	}

	
	/**
	 * Function for About us page content
	 **/
	@RequestMapping(value = "admin/aboutus", method = RequestMethod.GET)
	public ModelAndView aboutus(
			@ModelAttribute("command") StudentBean studentbean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		
		PageContentModel page_content=schoolservice.getAboutUs(1);
		model.addAttribute("content", page_content);
		model.addAttribute("heading", "About Us");
		return new ModelAndView("SuperAdmin/about_us", model);
	}

	/**
	 * Function for About us page content using post
	 **/
	@RequestMapping(value = "admin/aboutus", method = RequestMethod.POST)
	public ModelAndView aboutus(
			@ModelAttribute("command") StudentBean studentbean,
			BindingResult result, ModelMap model, HttpServletRequest request,
			PageContentBean page_bean) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		PageContentModel page_model = new PageContentModel();
		page_model.setP_id(null);
		page_model.setP_name("About us");
		page_model.setHeading1(page_bean.getHeading1());
		page_model.setHeading2(page_bean.getHeading2());
		page_model.setHeading3(page_bean.getHeading3());
		page_model.setDescription1(page_bean.getDescription1());
		page_model.setDescription2(page_bean.getDescription2());
		page_model.setDescription3(page_bean.getDescription3());
		page_model.setHeading4(page_bean.getHeading4());
		page_model.setHeading5(page_bean.getHeading5());
		page_model.setDescription4(page_bean.getDescription4());
		page_model.setDescription5("");
		schoolservice.updatePageContent(page_model, 1);
		PageContentModel page_content=schoolservice.getAboutUs(1);
		model.addAttribute("content", page_content);
		model.addAttribute("heading", "About Us");
		model.addAttribute("success", "Page updated successfully");
		return new ModelAndView("SuperAdmin/about_us", model);
	}

	/**
	 * Method for manage all school classes
	 **/
	@RequestMapping(value = "admin/manageClasses", method = RequestMethod.GET)
	public ModelAndView manageClasses(
			@ModelAttribute("command") SchoolClasses sClass,
			BindingResult result, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("class_info",
				ControllerHelper.getAllClassList(schoolservice.getAllSchoolClasses(school_id)));
		model.put("heading", "Manage Classes");
		return new ModelAndView("SuperAdmin/all_classes", model);
	}

	

	/**
	 * Method for manage Add new class
	 **/
	@RequestMapping(value = "admin/addClass", method = RequestMethod.GET)
	public ModelAndView addClass(
			@ModelAttribute("command") SchoolClasses sClass,
			BindingResult result, HttpServletRequest request, ModelMap model1) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		model.put("heading", "Add Class");
		return new ModelAndView("SuperAdmin/add_school_class", model);

	}

	/**
	 * Method for manage all school classes
	 **/
	@RequestMapping(value = "admin/addClass", method = RequestMethod.POST)
	public ModelAndView addClass(
			@ModelAttribute("command") SchoolClasses sClass,
			BindingResult result, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		Map<String, Object> model = new HashMap<String, Object>();
		int school_id = (Integer) session.getAttribute("new_school_id");
		model.put("school_details", schoolservice.getSchoolById(school_id));
		SchoolClassesModel cls = new SchoolClassesModel();
		cls.setClass_name(sClass.getClass_name());
		cls.setSchool_id(school_id);
		schoolservice.addSchoolClass(cls);
		model.put("heading", "Add Class");
		model.put("success", "Class added successfully");
		return new ModelAndView("SuperAdmin/add_school_class", model);

	}

	@RequestMapping(value = "admin/deleteClass", method = RequestMethod.GET)
	public ModelAndView deleteClass(
			@ModelAttribute("command") SchoolClasses sClass,
			BindingResult result, ModelMap modelH) {
		schoolservice.deleteClass(sClass.getClass_id());
		return new ModelAndView("redirect:manageClasses");
	}

	/**
	 * Function for send password to parent
	 **/
	@RequestMapping(value = "admin/sendDriverPassword", method = RequestMethod.POST)
	public ModelAndView sendDriverPassword(
			@ModelAttribute("command") LoginBean parent, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int user_id = parent.getUser_id();
		String contact_number = parent.getMobile_number()
				+ parent.getContact_number();
		String str=""+user_id+"";
		byte[] encodedBytes = Base64.encodeBase64(str.getBytes());
		String current_url = parent.getUser_pass() + "?q=" + new String(encodedBytes);
		String message = parent.getSchool_name();
		Sms_api sms = new Sms_api();
		sms.sendMsg(message,  contact_number);

		return new ModelAndView("redirect:" + current_url);
	}
	/**
	 * Function for send password to parent
	 **/
	@RequestMapping(value = "admin/sendDriverPassword1", method = RequestMethod.POST)
	public ModelAndView sendDriverPassword1(
			@ModelAttribute("command") LoginBean parent, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
	
		String contact_number = parent.getMobile_number()+ parent.getContact_number();
		String current_url = "/admin/addDriver/";
		String message = parent.getFirst_name();
		Sms_api sms = new Sms_api();
		sms.sendMsg(message,  contact_number);

		return new ModelAndView("redirect:" + current_url);
	}
	/**
	 * Function for send password to parent
	 **/
	@RequestMapping(value = "admin/sendDriverPassword2", method = RequestMethod.POST)
	public ModelAndView sendDriverPassword2(
			@ModelAttribute("command") LoginBean parent, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
	
		String contact_number = parent.getMobile_number()+ parent.getContact_number();
		String current_url = "/admin/addParent/";
		String message = parent.getFirst_name();
		Sms_api sms = new Sms_api();
		sms.sendMsg(message, contact_number);

		return new ModelAndView("redirect:" + current_url);
	}
	
	/**
	 * Function for send password to school admin
	 **/
	@RequestMapping(value = "admin/sendSchoolAdminSms", method = RequestMethod.POST)
	public ModelAndView sendSchoolAdminSms(
			@ModelAttribute("command") LoginBean parent, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		String contact_number = parent.getContact_number();
		String current_url = "/admin/addSchool/";
		String message = parent.getFirst_name();
		System.out.println(message);
		Sms_api sms = new Sms_api();
		sms.sendMsg(message, contact_number);
		
		return new ModelAndView("redirect:" + current_url);
	}
	
	
	/**
	 * Function for track student by route id
	 **/
	@RequestMapping(value = "admin/trackDriver", method = RequestMethod.GET)
	public ModelAndView trackDriver(
			@ModelAttribute("command") StudentBean studentbean,DriverBean driverbean
			,BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int driver_id=Integer.parseInt(new String(decodedBytes));	
			DriverModel drivermodel = schoolservice.getDriverById(driver_id);
			int school_id = (Integer) session.getAttribute("new_school_id");
			model.put("school_details", schoolservice.getSchoolById(school_id));
		    model.put("students", ControllerHelper.getDriverTrackList(studentservice.getDriverTrack(drivermodel.getRoute_id()), schoolservice));
		    model.put("all_students",
		    		ControllerHelper. getStudentList(schoolservice.getStudentsByRouteId(drivermodel.getRoute_id()),schoolservice));
			 
		    model.put("first_lat_lng", ControllerHelper.getDriverTrackList(studentservice.getDriverTrackLimit(drivermodel.getRoute_id()), schoolservice));
		    
			model.put("size_l",ControllerHelper.getDriverTrackList(studentservice.getDriverTrack(drivermodel.getRoute_id()), schoolservice).size() - 1);
			RouteModel routemodel =new RouteModel();
			routemodel = schoolservice.getRouteById(drivermodel.getRoute_id());
			if (routemodel != null) {
				model.put("latlong",ControllerHelper.getAllLatLng(schoolservice.listLatLng(drivermodel.getRoute_id()), studentservice));
				model.put("route", routemodel);
			}
			model.put("route_id", drivermodel.getRoute_id());
			model.put("driver_details", drivermodel);
			DriverAttendanceModel dam=new DriverAttendanceModel();
		    dam=schoolservice.getTodayDriverAttendance(driver_id);
		   // model.put("driver_login_date",dam.getLogin_time());
		    if(dam!=null)
			   {
				   model.put("driver_login_date",dam.getLogin_time());
			   }else
			   {
				   model.put("driver_login_date","");
			   }
			List<VechileBean> v=new ArrayList<VechileBean>();
			v=ControllerHelper.getDriverVehicleList(schoolservice.getVechileByDriverId(driver_id));
			if(v!=null)
			{
				  model.put("vehicle_details",ControllerHelper.getDriverVehicleList(schoolservice.getVechileByDriverId(driver_id)));
			}else
			{
				  model.put("vehicle_details","");
			}
		  
		    
		    
		    
		    
		} catch (Exception e) {
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int driver_id=Integer.parseInt(new String(decodedBytes));	
			DriverModel drivermodel = schoolservice.getDriverById(driver_id);
			RouteModel routemodel =new RouteModel();
			routemodel = schoolservice.getRouteById(drivermodel.getRoute_id());
			if (routemodel != null) {
				model.put("latlong",ControllerHelper.getAllLatLng(schoolservice.listLatLng(drivermodel.getRoute_id()), studentservice));
				model.put("route", routemodel);
			}
			model.put("all_students", ControllerHelper.
					getStudentList(schoolservice.getStudentsByRouteId(drivermodel.getRoute_id()) , schoolservice));
			model.put("route_id", drivermodel.getRoute_id());
			DriverAttendanceModel dam=new DriverAttendanceModel();
		    dam=schoolservice.getTodayDriverAttendance(driver_id);
		   if(dam!=null)
		   {
			   model.put("driver_login_date",dam.getLogin_time());
		   }else
		   {
			   model.put("driver_login_date","");
		   }
		    
		    model.put("driver_details", drivermodel);
		    
			model.put("error", "No data available");
			//model.put("vehicle_details",getDriverVehicleList(schoolservice.getVechileByDriverId(driver_id)));
			List<VechileBean> v=new ArrayList<VechileBean>();
			v=ControllerHelper.getDriverVehicleList(schoolservice.getVechileByDriverId(driver_id));
			if(v!=null)
			{
				  model.put("vehicle_details",ControllerHelper.getDriverVehicleList(schoolservice.getVechileByDriverId(driver_id)));
			}else
			{
				  model.put("vehicle_details","");
			}
			e.printStackTrace();
		}
		
		return new ModelAndView("SuperAdmin/track_driver", model);
	}

	/**
	 * Function for view Student
	 **/
	@RequestMapping(value = "admin/viewDriverAttendance", method = RequestMethod.GET)
	public ModelAndView viewDriverAttendance(
			@ModelAttribute("command") StudentBean students,DriverBean driverbean,
			BindingResult result, HttpServletRequest request) {
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int driver_id=Integer.parseInt(new String(decodedBytes));
		DriverModel drivermodel = schoolservice.getDriverById(driver_id);
		Map<String, Object> model = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		int school_id = (Integer) session.getAttribute("new_school_id");

		if (drivermodel != null) {
			try {
			
				model.put("heading", "View Driver Attendance");
				model.put("student", drivermodel);
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				model.put("current_date", dateFormat.format(date));
				model.put("all_holiday",
						ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id)));
				model.put("all_present", ControllerHelper.getDriverPresentList(schoolservice
						.getDriverAttendanceList(driver_id)));

			} catch (Exception e) {
				e.printStackTrace();
			}

			model.put("school_details", schoolservice.getSchoolById(school_id));
			
			
			
			Calendar now = Calendar.getInstance();
			
			int iMonth=0;
			int year_num=now.get(Calendar.YEAR);
			
			String month=""+(now.get(Calendar.MONTH) + 1);
			
			System.out.println("--month"+month);
			if((now.get(Calendar.MONTH) + 1)==01)
			{
				month="01";
				iMonth =Calendar.JANUARY;
			}else if ((now.get(Calendar.MONTH) + 1)==02) {
				month="02";
				iMonth =Calendar.FEBRUARY;
			}else if ((now.get(Calendar.MONTH) + 1)==03) {
				month="03";
				iMonth =Calendar.MARCH;
			}else if ((now.get(Calendar.MONTH) + 1)==04) {
				month="04";
				iMonth =Calendar.APRIL;
			}else if ((now.get(Calendar.MONTH) + 1)==05) {
				month="05";
				iMonth =Calendar.MAY;
			}else if ((now.get(Calendar.MONTH) + 1)==06) {
				month="06";
				iMonth =Calendar.JUNE;
			}else if ((now.get(Calendar.MONTH) + 1)==07) {
				month="07";
				iMonth =Calendar.JULY;
			}else if ((now.get(Calendar.MONTH) + 1)==8) {
				month="08";
				iMonth =Calendar.AUGUST;
			}else if ((now.get(Calendar.MONTH) + 1)==9) {
				month="09";
				iMonth =Calendar.SEPTEMBER;
			}else if ((now.get(Calendar.MONTH) + 1)==10) {
				month="10";
				iMonth =Calendar.OCTOBER;
			}else if ((now.get(Calendar.MONTH) + 1)==11) {
				month="11";
				iMonth =Calendar.NOVEMBER;
			}else {
				month="12";
				iMonth =Calendar.DECEMBER;
			}
			
			String year=""+year_num;
			int total_present=0;
			int absent_day=0;
			total_present=schoolservice.getDriverAttendanceListByMonth(driver_id,month,year).size();
			int iYear = Integer.parseInt(year.trim());
			int iDay = 1;
			Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);
			int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
			System.out.println("Day in month="+daysInMonth);
			List<HolidayBean> b=ControllerHelper.getHolidayList(schoolservice.getAllHolidayByMonth(school_id,month,year));
			List<HolidayDeletedBean> c=ControllerHelper.getDeletedHolidayList(schoolservice.getAllDeletedHoliday(school_id));
			try{
			for(int i=0;i<b.size();i++)
			{
				for(int j=0;j<c.size();j++)
				{
					if(b.get(i).getH_id().equals(c.get(j).getHoliday_id()))
					{
						b.remove(i);
					}
				}
			}
			}catch(Exception e)
			{
				System.out.println(e);
			}
			
			if(b!=null)
			{
				absent_day=absent_day- b.size();
				model.put("holiday_days", b.size());
			}else
			{
				model.put("holiday_days",0);
			}
			absent_day=daysInMonth-total_present;
			model.put("present_day", total_present);
			model.put("absent_day", absent_day);
			
			
			
			
			
			
			return new ModelAndView("SuperAdmin/view_driver_attendance", model);
		} else {
			return new ModelAndView("redirect:manageDrivers");
			}
		}
		catch(Exception e)
		{
			return new ModelAndView("redirect:manageDrivers");
		}

	}
	
	/**
	 *Method for manage country 
	 **/
	@RequestMapping(value = "admin/manageCountry", method = RequestMethod.GET)
	public ModelAndView manageCountry(
			@ModelAttribute("command") CountryBean country,
			BindingResult result, HttpServletRequest request) {
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try {
			Map<String, Object> modelM = new HashMap<String, Object>();
			modelM.put("countries",ControllerHelper. prepareListofBean(schoolservice.listCountry()));
			modelM.put("heading", "Manage Country");
			return new ModelAndView("SuperAdmin/all_country", modelM);
		} catch (Exception e) {
			return new ModelAndView("redirect:manageCountry");
		}
	
	}
	/**
	 * Function for add new country
	 **/
	@RequestMapping(value="admin/addCountry", method = RequestMethod.GET)
	public ModelAndView addCountry(@ModelAttribute("command")  CountryBean country,BindingResult result,HttpServletRequest request)
	{
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		Map<String, Object> model=new HashMap<String,Object>();
		model.put("heading", "Add Country");
		return new ModelAndView("SuperAdmin/add_country", model);
	}
	/**
	 *Function for add new country with post method 
	 **/
	@RequestMapping(value="admin/addCountry", method = RequestMethod.POST)
	public ModelAndView addCountry(@ModelAttribute("command") ModelMap model, CountryBean country, BindingResult result, HttpServletRequest request)
	{	
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try{
		 
		CountryModel found = schoolservice.countryCheck(country.getC_name());
		if(found==null)
		{	
			CountryModel found1 = schoolservice.countryCodeCheck(country.getC_code());
			if(found1==null)
			{
				CountryModel addCountry=new CountryModel();
				addCountry.setC_code(country.getC_code());
				addCountry.setC_name(country.getC_name());
				schoolservice.addCountry(addCountry);
				model.addAttribute("success", "Country added successfully!");
				return new ModelAndView("SuperAdmin/add_country", model);
				//return new ModelAndView("redirect:/admin/manageCountry",model);
			}
			else
			{
				model.addAttribute("error", "Country code already exist");
				return new ModelAndView("SuperAdmin/add_country",model);
			}
			
		} else {
			model.addAttribute("error", "Country name already exist");
			return new ModelAndView("SuperAdmin/add_country",model);
		}
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageCountry");
		}
		
		 
	}
	/**
	 *Method for delete country 
	 **/
	@RequestMapping(value="admin/deleteCountry", method = RequestMethod.GET)
	public ModelAndView deleteCountry(@ModelAttribute("command") ModelMap model, CountryBean country, BindingResult result, HttpServletRequest request)
	{
		schoolservice.deleteCountry(country.getC_id());
		return new ModelAndView("redirect:manageCountry");
	}
	/**
	 *Method for update country by country id 
	 **/
	@RequestMapping(value="admin/editCountry", method=RequestMethod.GET)
	public ModelAndView editCountry(@ModelAttribute("command") ModelMap model, CountryBean country, BindingResult result, HttpServletRequest request)
	{
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int c_id=Integer.parseInt(new String(decodedBytes));
		model.put("country_details",schoolservice.getCountryById(c_id));
		return new ModelAndView("SuperAdmin/add_country",model);
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageCountry");
		}
	}
	
	/**
	 *Method for update country by country id 
	 **/
	@RequestMapping(value="admin/editCountry", method=RequestMethod.POST)
	public ModelAndView editCountry(@ModelAttribute("command") CountryBean country, BindingResult result, HttpServletRequest request,ModelMap model)
	{
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int c_id=Integer.parseInt(new String(decodedBytes));
			CountryModel beans=new CountryModel();
			beans.setC_code(country.getC_code());
			beans.setC_name(country.getC_name());
			schoolservice.editCountryById(c_id, beans);
			model.put("country_details",schoolservice.getCountryById(c_id));
			model.put("success", "Country updated successfully!");
			return new ModelAndView("SuperAdmin/add_country",model);
		}catch(Exception e)
		{
			 return new ModelAndView("redirect:manageCountry");
		}
	}
	/**
	 *Method for manage Towns 
	 **/
	@RequestMapping(value = "admin/manageTown", method = RequestMethod.GET)
	public ModelAndView manageTown(
			@ModelAttribute("command") CityBean city,
			BindingResult result, HttpServletRequest request) {
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try {
			Map<String, Object> modelM = new HashMap<String, Object>();
			modelM.put("countries",ControllerHelper. prepareListofBean(schoolservice.listCountry()));
			modelM.put("towns",ControllerHelper. prepareListofCities(schoolservice.listCities()));
			modelM.put("heading", "Manage Towns");
			return new ModelAndView("SuperAdmin/all_cities", modelM);
		} catch (Exception e) {
			return new ModelAndView("redirect:manageTown");
		}
	
	}
	
	/**
	 *Function for add new country with post method 
	 **/
	@RequestMapping(value = "admin/addTown", method = RequestMethod.POST)
	public ModelAndView addTown(@ModelAttribute("command") ModelMap model,
			CityBean city, BindingResult result,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole") != "Admin") {
			return new ModelAndView("redirect:/login.html");
		}
		 
		model.put("heading", "Add City");
		model.put("countries",ControllerHelper. prepareListofBean(schoolservice.listCountry()));
		try {
			
			CityModel found = schoolservice.cityCheck(city.getCity_name(),city.getCountry_id());
			if (found == null) {
					CityModel addCity = new CityModel();
					addCity.setCity_name(city.getCity_name());
					addCity.setCountry_id(city.getCountry_id());
					addCity.setState_id(0);
					schoolservice.addCity(addCity);
					model.addAttribute("success", "Town added successfully!");
					return new ModelAndView("SuperAdmin/add_cities", model);
				 

			} else {
				model.addAttribute("error", "Town name already exist");
				return new ModelAndView("SuperAdmin/add_cities", model);
			}
		} catch (Exception e) {
			return new ModelAndView("redirect:manageTown");
		}

	}
	/**
	 * Function for add new country
	 **/
	@RequestMapping(value="admin/addTown", method = RequestMethod.GET)
	public ModelAndView addTown(@ModelAttribute("command")  CountryBean country,BindingResult result,HttpServletRequest request)
	{
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		Map<String, Object> model=new HashMap<String,Object>();
		model.put("heading", "Add City");
		model.put("countries", ControllerHelper.prepareListofBean(schoolservice.listCountry()));
		return new ModelAndView("SuperAdmin/add_cities", model);
	}
	/**
	 *Method for update city 
	 **/
	@RequestMapping(value="admin/editCity", method=RequestMethod.GET)
	public ModelAndView editCity(@ModelAttribute("command") ModelMap model, CityBean country, BindingResult result, HttpServletRequest request)
	{
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int c_id=Integer.parseInt(new String(decodedBytes));
		model.put("city_details",schoolservice.getCityById(c_id));
		model.put("countries",ControllerHelper. prepareListofBean(schoolservice.listCountry()));
		return new ModelAndView("SuperAdmin/add_cities",model);
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageTown");
		}
	}
	/**
	 *Method for update country by country id 
	 **/
	@RequestMapping(value="admin/editCity", method=RequestMethod.POST)
	public ModelAndView editCity(@ModelAttribute("command") CityBean city, BindingResult result, HttpServletRequest request,ModelMap model)
	{
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int c_id=Integer.parseInt(new String(decodedBytes));
			CityModel beans=new CityModel();
			beans.setCity_name(city.getCity_name());
			beans.setCountry_id(city.getCountry_id());
			schoolservice.editCityById(c_id, beans);
			model.put("city_details",schoolservice.getCityById(c_id));
			model.put("countries", ControllerHelper.prepareListofBean(schoolservice.listCountry()));
			model.put("success", "Town updated successfully!");
			return new ModelAndView("SuperAdmin/add_cities",model);
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageTown");
		}
	}
	/**
	 *Method for delete country 
	 **/
	@RequestMapping(value="admin/deleteCity", method = RequestMethod.GET)
	public ModelAndView deleteCity(@ModelAttribute("command") ModelMap model, CityBean city, BindingResult result, HttpServletRequest request)
	{
		schoolservice.deleteCity(city.getCity_id());
		return new ModelAndView("redirect:manageTown");
	}
	
	
	
	/**
	 *Method for delete country 
	 **/
	@RequestMapping(value="admin/addNationlities", method = RequestMethod.GET)
	public ModelAndView nationlities(@ModelAttribute("command") ModelMap model, NationlityBean nationBean, BindingResult result, HttpServletRequest request)
	{
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		model.put("heading", "Add Nationlity");
		return new ModelAndView("SuperAdmin/add_nationlity",model);
	}
	/**
	 *Function for add new nationlity with post method 
	 **/
	@RequestMapping(value="admin/addNationlities", method = RequestMethod.POST)
	public ModelAndView addNationlities(@ModelAttribute("command") ModelMap model, NationlityBean nationBean, BindingResult result, HttpServletRequest request)
	{	
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try{
		 
		NationlityModel found = schoolservice.checkNationlity(nationBean.getName());
		if(found==null)
		{	 
			NationlityModel nationModel=new NationlityModel();
			nationModel.setName(nationBean.getName());
			schoolservice.addNationlity(nationModel);
			model.addAttribute("success", "Nationality added successfully!");
			return new ModelAndView("SuperAdmin/add_nationlity",model);
			 
			
		} else {
			model.addAttribute("error", "Nationality already exist");
			model.put("heading", "Add Nationlity");
			return new ModelAndView("SuperAdmin/add_nationlity",model);
		}
		}catch(Exception e)
		{
			return new ModelAndView("redirect:manageNationlities");
		}
		
		 
	}
	/*Method for manage all nationlity*/
	@RequestMapping(value="admin/manageNationlities",method=RequestMethod.GET)
	public ModelAndView manageNationlities(@ModelAttribute("command") ModelMap model,NationlityBean nationBean, BindingResult result, HttpServletRequest request)
	{
		HttpSession session=request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		try{
			model.put("heading", "Manage Nationality");
			model.put("nationlity",ControllerHelper.prepareNationalityList(schoolservice.getAllNationlity()));
			return new ModelAndView("SuperAdmin/all_nationality",model);
		}catch(Exception e)
		{
			e.printStackTrace();
			return new ModelAndView("redirect:manageNationlities");
		}
		 
	}

	/**
	 * Function for edit student
	 **/
	@RequestMapping(value = "admin/editNationlity", method = RequestMethod.GET)
	public ModelAndView editNationlity(
			@ModelAttribute("command") NationlityBean nationBean,
			BindingResult result, HttpServletRequest request) {
		
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int national_id=Integer.parseInt(new String(decodedBytes));
		NationlityModel studentModel = studentservice.getNationalityById(national_id);
		Map<String, Object> model = new HashMap<String, Object>();
		if (studentModel != null) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			model.put("nation", studentModel);
			model.put("heading", "Update Nationality");
			return new ModelAndView("SuperAdmin/add_nationlity", model);
		} else {
			return new ModelAndView("redirect:manageNationlities");
		}
		}catch(Exception e)
		{	System.out.println(e);
		return new ModelAndView("redirect:manageNationlities");
		}
		

	}
	/**
	 * Function for edit student
	 **/
	@RequestMapping(value = "admin/editNationlity", method = RequestMethod.POST)
	public ModelAndView editNationlity(
			@ModelAttribute("command") NationlityBean nationBean,
			BindingResult result, HttpServletRequest request,ModelMap model) {
		
		try{
		String q=request.getParameter("q");
		byte[] decodedBytes = Base64.decodeBase64(""+q+"");
		int national_id=Integer.parseInt(new String(decodedBytes));
		NationlityModel nation=new NationlityModel();
		nation.setName(nationBean.getName());
		studentservice.updateNationalityById(nation,national_id);
		return new ModelAndView("redirect:manageNationlities");
		}catch(Exception e)
		{	System.out.println(e);
		return new ModelAndView("redirect:manageNationlities");
		}
		

	}
	@RequestMapping(value = "admin/deleteNationlity", method = RequestMethod.GET)
	public ModelAndView deleteNationlity(
			@ModelAttribute("command") NationlityBean nationBean, BindingResult result,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userRole")!= "Admin")
		{
		 return new ModelAndView("redirect:/login.html");
		}
		schoolservice.deleteNationlity(nationBean.getNational_id());
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView("redirect:manageNationlities");
	}
	@RequestMapping(value = "admin/addWeekendHoliday", method = RequestMethod.POST)
	public ModelAndView addWeekendHoliday(
			@ModelAttribute("command") HolidayBean holidaybean,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
		 
			String holiday1 = holidaybean.getHoliday_date();
			String[] parts = holiday1.split("-");
			int year1 =Integer.parseInt(parts[0]); // 004
			int month1 = Integer.parseInt(parts[1]); // 034556
			int day1 = Integer.parseInt(parts[2]); // 034556
			
			
			
			String holiday2 = holidaybean.getHoliday_enddate();
			String[] parts2 = holiday2.split("-");
			int year2 = Integer.parseInt(parts2[0]); // 004
			int month2 = Integer.parseInt(parts2[1]); // 034556
			int day2 = Integer.parseInt(parts2[2]); // 034556
			
			System.out.println(holidaybean.getDay());
			List<LocalDate> dates = ControllerHelper.getWeekendDates(new LocalDate(year1,month1,day1), new LocalDate(year2,month2,day2),holidaybean.getDay());
	        for (LocalDate date : dates)
	        {
	        	System.out.println(date);
	        	int school_id = (Integer) session.getAttribute("new_school_id");
				HolidayModel holidaymodel = new HolidayModel();
				holidaymodel.setH_id(null);
				holidaymodel.setHoliday_date(date.toString());
				holidaymodel.setHoliday_name(holidaybean.getHoliday_name());
				holidaymodel.setHoliday_enddate(date.toString());
				holidaymodel.setSchool_id(holidaybean.getSchool_id());
				schoolservice.addHoliday(holidaymodel);
	            System.out.println(date);
	        }
			model.addAttribute("success", "Holiday Added Successfully");
			model.addAttribute("heading", "Add Holiday");

		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpSession session = request.getSession();

		int school_id = (Integer) session.getAttribute("new_school_id");
		model.addAttribute("heading", "View Holiday Calendar");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy");
		DateFormat dateFormat2 = new SimpleDateFormat("MM");

		Date date = new Date();
		int year = Integer.parseInt(dateFormat1.format(date));
		int month = Integer.parseInt(dateFormat2.format(date));
		model.put("school_id", school_id);
		model.put("current_date", dateFormat.format(date));
		model.put("all_holiday",
				ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id)));
		model.put("holidays", ControllerHelper.getHolidays(frontService.getHolidayByMonth(month,
				year, school_id)));
		model.put("school_details", schoolservice.getSchoolById(school_id));
		return new ModelAndView("redirect:viewCalendar");

		/*
		 * int school_id=(Integer) session.getAttribute("new_school_id");
		 * model.put("school_details", schoolservice.getSchoolById(school_id));
		 * return new ModelAndView("SuperAdmin/add_holiday");
		 */

	}
		
		
		@RequestMapping(value = "admin/addAdminWeekEndHoliday", method = RequestMethod.POST)
		public ModelAndView addAdminWeekEndHoliday(
				@ModelAttribute("command") HolidayBean holidaybean,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			try {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				String holiday1 = holidaybean.getHoliday_date();
				String[] parts = holiday1.split("-");
				int year1 =Integer.parseInt(parts[0]); // 004
				int month1 = Integer.parseInt(parts[1]); // 034556
				int day1 = Integer.parseInt(parts[2]); // 034556
				
				
				
				String holiday2 = holidaybean.getHoliday_enddate();
				String[] parts2 = holiday2.split("-");
				int year2 = Integer.parseInt(parts2[0]); // 004
				int month2 = Integer.parseInt(parts2[1]); // 034556
				int day2 = Integer.parseInt(parts2[2]); // 034556
				
				System.out.println(holidaybean.getDay());
				List<LocalDate> dates = ControllerHelper.getWeekendDates(new LocalDate(year1,month1,day1), new LocalDate(year2,month2,day2),holidaybean.getDay());
		        for (LocalDate date : dates)
		        {
		        	int school_id = 0;
					HolidayModel holidaymodel = new HolidayModel();
					holidaymodel.setH_id(null);
					holidaymodel.setHoliday_date(date.toString());
					holidaymodel.setHoliday_name(holidaybean.getHoliday_name());
					holidaymodel.setHoliday_enddate(date.toString());
					holidaymodel.setSchool_id(0);
					schoolservice.addHoliday(holidaymodel);
		        	
		        }
				
				
				model.addAttribute("success", "Holiday Added Successfully");
				model.addAttribute("heading", "Add Holiday");

			} catch (Exception e) {
				e.printStackTrace();
			}
			HttpSession session = request.getSession();

			int school_id = 0;
			model.addAttribute("heading", "View Holiday Calendar");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat dateFormat1 = new SimpleDateFormat("yyyy");
			DateFormat dateFormat2 = new SimpleDateFormat("MM");

			Date date = new Date();
			int year = Integer.parseInt(dateFormat1.format(date));
			int month = Integer.parseInt(dateFormat2.format(date));
			model.put("school_id", school_id);
			model.put("current_date", dateFormat.format(date));
			model.put("all_holiday",
					ControllerHelper.getHolidayList(schoolservice.getAllHoliday(school_id)));
			model.put("holidays", ControllerHelper.getHolidays(frontService.getHolidayByMonth(month,
					year, school_id)));
			return new ModelAndView("redirect:viewAdminCalendar");

			/*
			 * int school_id=(Integer) session.getAttribute("new_school_id");
			 * model.put("school_details", schoolservice.getSchoolById(school_id));
			 * return new ModelAndView("SuperAdmin/add_holiday");
			 */

		}

		/**
		 * Function for edit parent
		 **/
		@RequestMapping(value = "admin/deleteAdminHoliday", method = RequestMethod.GET)
		public ModelAndView deleteAdminHoliday(
				@ModelAttribute("command") HolidayBean holiday,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			try{
			int h_id=holiday.getH_id();	
			HolidayModel holidaymodel = new HolidayModel();
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int school_id = (Integer) session.getAttribute("new_school_id");
			holidaymodel = schoolservice.getHolidayById(h_id);
			
			HolidayDeletedModel hdm=new HolidayDeletedModel();
			hdm.setHoliday_id(holidaymodel.getH_id());
			hdm.setSchool_id(school_id);
			schoolservice.schoolDeletedHoliday(hdm);
			model.put("success", "Holiday deleted successfully");
			return new ModelAndView("redirect:manageHoliday", model);
			}catch(Exception e)
			{
				return new ModelAndView("redirect:manageHoliday");
			}
		}

	
		/**
		 * Function for edit parent
		 **/
		@RequestMapping(value = "admin/deleteMultipleHoliday", method = RequestMethod.POST)
		public ModelAndView deleteMultipleHoliday(
				@ModelAttribute("command") HolidayBean holiday,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			try{
		
			HolidayModel holidaymodel = new HolidayModel();
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int j=0;
			List<String> items = Arrays.asList(holiday.getHoliday_name().split("\\s*,\\s*"));
			List<String> items_school = Arrays.asList(holiday.getHoliday_date().split("\\s*,\\s*"));
			 
			for(String a : items)
			{	
				if(items_school.get(j).equals("0"))
				{
					int school_id = (Integer) session.getAttribute("new_school_id");
					holidaymodel = schoolservice.getHolidayById(Integer.parseInt(a));
					HolidayDeletedModel hdm=new HolidayDeletedModel();
					hdm.setHoliday_id(holidaymodel.getH_id());
					hdm.setSchool_id(school_id);
					schoolservice.schoolDeletedHoliday(hdm);
					
				}else{
					schoolservice.deleteHoliday(Integer.parseInt(a));
				}
			
				j++;
			}
			
			return new ModelAndView("redirect:manageHoliday", model);
			}catch(Exception e)
			{
				e.printStackTrace();
				return new ModelAndView("redirect:manageHoliday");
			}
			
		}
		/**
		 * Function for edit parent
		 **/
		@RequestMapping(value = "admin/deleteMultipleAdminHoliday", method = RequestMethod.POST)
		public ModelAndView deleteMultipleAdminHoliday(
				@ModelAttribute("command") HolidayBean holiday,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			try{
			HolidayModel holidaymodel = new HolidayModel();
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int j=0;
			List<String> items = Arrays.asList(holiday.getHoliday_name().split("\\s*,\\s*"));
			List<String> items_school = Arrays.asList(holiday.getHoliday_date().split("\\s*,\\s*"));
			 
			for(String a : items)
			{	
				if(items_school.get(j).equals("0"))
				{
					schoolservice.deleteHoliday(Integer.parseInt(a));
					
				}else{
					schoolservice.deleteHoliday(Integer.parseInt(a));
				}
			
				j++;
			}
			
			return new ModelAndView("redirect:manageAdminHoliday");
			}catch(Exception e)
			{
				return new ModelAndView("redirect:manageAdminHoliday");
			}
			
		}
		/**
		 * Function for track all drivers
		 **/
		@RequestMapping(value = "admin/trackDrivers", method = RequestMethod.GET)
		public ModelAndView trackDrivers(
				@ModelAttribute("command") StudentBean studentbean,DriverBean driverbean
				,BindingResult result, ModelMap model, HttpServletRequest request) {
			try {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				
				DriverModel drivermodel = null;
				int school_id = (Integer) session.getAttribute("new_school_id");
				
				List<RouteModel> routes=new ArrayList<RouteModel>();
				routes= schoolservice.listRoute(school_id);
				Map<Integer, List<DriverTrack>> map = new HashMap<Integer, List<DriverTrack>>();
				
				int i=0;
				int total=0;
				for (RouteModel st : routes) {
					List<DriverTrack> driver_lat_lng=new ArrayList<DriverTrack>();	
					driver_lat_lng=ControllerHelper.getDriverTrackList(studentservice.getDriverTrackLimitOne(st.getRoute_id()), schoolservice);
					
					if(driver_lat_lng!=null)
					{ 	
						
						map.put(i,driver_lat_lng);
						total=total+studentservice.getDriverTrack(st.getRoute_id()).size();
					}
				i++;
				}
				 
				
				model.put("drivers",ControllerHelper. getDriversList(schoolservice.listDriver(school_id),schoolservice));
				model.put("school_id",school_id);
				model.put("school_details", schoolservice.getSchoolById(school_id));
			    model.put("students", ControllerHelper.getDriverTrackList(studentservice.getDriverTrack(0), schoolservice));
				model.put("first_lat_lng", ControllerHelper.getDriverTrackList(studentservice.getDriverTrackLimit(0), schoolservice));
				
				if(map.isEmpty())
				{
					SchoolModel sc_details=new SchoolModel();
					sc_details=schoolservice.getSchoolById(school_id);
					List<DriverTrack> dtBean=new ArrayList<DriverTrack>();	
					DriverTrack	Dbean = new DriverTrack();	
					Dbean.setLat(sc_details.getSchool_lat());
					Dbean.setLng(sc_details.getSchool_lng());
					dtBean.add(Dbean);
					map.put(i,dtBean);
					model.put("lat_lng", map);
				//	model.put("size_l", map.size());
				}else
				{
				//	model.put("size_l", map.size());
					model.put("lat_lng", map);
				}
				model.put("size_l",total);
				//model.put("size_l",ControllerHelper.getDriverTrackList(studentservice.getDriverTrack(0)).size() - 1);
				RouteModel routemodel =new RouteModel();
				routemodel = schoolservice.getRouteById(0);
				if (routemodel != null) {
					model.put("latlong",ControllerHelper.getAllLatLng(schoolservice.listLatLng(0), studentservice));
					model.put("route", routemodel);
				}
				//model.put("route_id", drivermodel.getRoute_id());
			} catch (Exception e) {
				
				
				DriverModel drivermodel =null;
				RouteModel routemodel =new RouteModel();
				HttpSession session = request.getSession();
				int school_id = (Integer) session.getAttribute("new_school_id");
				model.put("drivers", ControllerHelper. getDriversList(schoolservice.listDriver(school_id), schoolservice));
				routemodel = schoolservice.getRouteById(0);
				if (routemodel != null) {
					model.put("latlong",ControllerHelper.getAllLatLng(schoolservice.listLatLng(0), studentservice));
					model.put("route", routemodel);
				}
				model.put("route_id", 0);
				model.put("error", "No data available1");
				e.printStackTrace();
			}
			
			return new ModelAndView("SuperAdmin/track_all_drivers", model);
		}
		
		/**
		 * Function for chatting in super admin with school admin
		 **/
		@RequestMapping(value = "admin/chattingSocket", method = RequestMethod.GET)
		public ModelAndView chattingSocket(
				@ModelAttribute("command") StudentBean studentbean,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			int user_id = (Integer) session.getAttribute("user_id");
			model.put("User_name", "SuperAdmin");
			model.put("User_id", user_id);
			//model.put("school_admins",ControllerHelper.getSchoolAdminList(schoolservice.listSchoolAdmin(0), user_id));
			model.put("school_admins",ControllerHelper.getSchoolAdminList(schoolservice.listSchoolAdminLatest(0), user_id, schoolservice));
			model.put("schools",ControllerHelper. prepareListOfSchool(schoolservice.listSchools()));
			return new ModelAndView("SuperAdmin/chatting_socket",model);
		}
		/**
		 * Function for edit vehicle
		 **/
		@RequestMapping(value = "admin/viewVechile", method = RequestMethod.GET)
		public ModelAndView viewVechile(
				@ModelAttribute("command") VechileBean vechile,
				BindingResult result, HttpServletRequest request) {
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int vehicle_id=Integer.parseInt(new String(decodedBytes));
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			VehicleModel vehiclemodel = vechileservice.getVehicleById(vehicle_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (vehiclemodel != null) {
				model.put("heading", "View Vehicle");
				model.put("vechileBean", vehiclemodel);
				model.put("schools",
						ControllerHelper.prepareListOfSchool(schoolservice.listSchools()));
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				int school_id = (Integer) session.getAttribute("new_school_id");
				model.put("school_details", schoolservice.getSchoolById(school_id));
				model.put("documents",ControllerHelper. prepareListOfVehicleDoc(schoolservice.getVehicleDocument(vehiclemodel.getVechile_id())));
				
				String ed=request.getParameter("v");
				if(ed != null)
				{
					byte[] decodedBytes1 = Base64.decodeBase64(""+ed+"");
					schoolservice.updateHeadNotification(Integer.parseInt(new String(decodedBytes1)));
					
				}
				return new ModelAndView("SuperAdmin/view_vehicle", model);
			} else {
				return new ModelAndView("redirect:manageVehicle");
			}
			}catch(Exception e)
			{
				return new ModelAndView("redirect:manageVehicle");
			}

		}
		
		@RequestMapping(value = "admin/manageRelationship", method = RequestMethod.GET)
		public ModelAndView manageRelationship(
				@ModelAttribute("command") relationshipBean relationship_bean,
				BindingResult result, ModelMap modelH, HttpServletRequest request) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			modelH.addAttribute("heading", "Manage Relationship");
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("relationship",ControllerHelper. getRelationshipList(schoolservice.listRelationship()));
			return new ModelAndView("SuperAdmin/all_relationship", model);
		}
		
		
		@RequestMapping(value = "admin/addRelationship", method = RequestMethod.GET)
        public ModelAndView addRelationship(
				@ModelAttribute("command") relationshipBean relationship_bean,
				BindingResult result, ModelMap modelH, HttpServletRequest request) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			modelH.addAttribute("heading", "Add Relationship");
			Map<String, Object> model = new HashMap<String, Object>();
			return new ModelAndView("SuperAdmin/add_relationship", model);
		}
		
		/**
		 *Function for add new country with post method 
		 **/
		@RequestMapping(value="admin/addRelationship", method = RequestMethod.POST)
		public ModelAndView addRelationship(@ModelAttribute("command") ModelMap model, relationshipBean relationship_bean, BindingResult result, HttpServletRequest request)
		{	
			HttpSession session=request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			try{
			 
            relationshipModel found = schoolservice.checkRelationship(relationship_bean.getR_title());
			if(found==null)
			{	
					relationshipModel rel=new relationshipModel();
					rel.setR_id(null);
					rel.setR_title(relationship_bean.getR_title());
					schoolservice.addRelationship(rel);
					model.addAttribute("success", "Relationship added successfully!");
					model.addAttribute("heading", "Add Relationship");
					return new ModelAndView("SuperAdmin/add_relationship", model);
				
				
			}else {
				model.addAttribute("error", "Relationship already exists");
				return new ModelAndView("SuperAdmin/add_relationship", model);
			}
			}catch(Exception e)
			{
				return new ModelAndView("redirect:manageRelationship");
			}
			
			 
		}
		
		/**
		 *Method for update relationship by relationship id
		 **/
		@RequestMapping(value="admin/editRelationship", method=RequestMethod.GET)
		public ModelAndView editRelationship(@ModelAttribute("command") ModelMap model, relationshipBean relationship_bean, BindingResult result, HttpServletRequest request)
		{
			HttpSession session=request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int c_id=Integer.parseInt(new String(decodedBytes));
			model.put("relationship",schoolservice.getRelationshipById(c_id));
			model.addAttribute("heading", "Update Relationship");
			return new ModelAndView("SuperAdmin/add_relationship", model);
			}catch(Exception e)
			{
				return new ModelAndView("redirect:manageRelationship");
			}
		}
		/**
		 *Method for update country by country id 
		 **/
		@RequestMapping(value="admin/editRelationship", method=RequestMethod.POST)
		public ModelAndView editRelationship(@ModelAttribute("command") relationshipBean relationship_bean, BindingResult result, HttpServletRequest request,ModelMap model)
		{
			try {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				
				String q=request.getParameter("q");
				byte[] decodedBytes = Base64.decodeBase64(""+q+"");
				int c_id=Integer.parseInt(new String(decodedBytes));
				relationshipModel rel=new relationshipModel();
				rel.setR_id(null);
				rel.setR_title(relationship_bean.getR_title());
				schoolservice.editRelationById(c_id, rel);
				model.addAttribute("heading", "Update Relationship");
				model.put("success", "Relationship updated successfully!");
				return new ModelAndView("redirect:manageRelationship",model);
			}catch(Exception e)
			{
				
				 return new ModelAndView("redirect:manageRelationship");
			}
		}
		/**
		 *Method for delete country 
		 **/
		@RequestMapping(value="admin/deleteRelationship", method = RequestMethod.GET)
		public ModelAndView deleteRelationship(@ModelAttribute("command") ModelMap model, relationshipBean relationship_bean, BindingResult result, HttpServletRequest request)
		{
			try{
				String q=request.getParameter("q");
				byte[] decodedBytes = Base64.decodeBase64(""+q+"");
				int c_id=Integer.parseInt(new String(decodedBytes));
				schoolservice.deleteRelationship(c_id);
				return new ModelAndView("redirect:manageRelationship");
				
			}catch(Exception e)
			{
				 return new ModelAndView("redirect:manageRelationship");
			}
			
		}
		/**
		 *Method for manage Slider 
		 **/
		@RequestMapping(value="admin/manage_slider",method=RequestMethod.GET)
		public ModelAndView manage_slider(@ModelAttribute("command") ModelMap model,SliderBean slider,HttpServletRequest request)
		{
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("sliders", ControllerHelper.iterateAllSlider(schoolservice.getAllSlider()));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_slider",model);
		}
		
		
		

		/**
		 * @access public
		 * Method for add slider 
		 * */		
		@RequestMapping(value="admin/addSlider",method=RequestMethod.GET)
		public ModelAndView addSlider(@ModelAttribute("command") SliderBean slider_bean,BindingResult result,ModelMap model,HttpServletRequest request)
		{
			try{
				HttpSession session=request.getSession();
				if(session.getAttribute("userRole")!="Admin")
				{
					return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("heading", "Add Slider");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/add_slider");
		}
		/**
		 * @access public
		 *Method for save slider image 
		 **/
		@RequestMapping(value="admin/addSlider", method=RequestMethod.POST)
		public ModelAndView addSlider(@ModelAttribute("command") SliderBean s_bean,BindingResult result,HttpServletRequest request)
		{
			ModelMap model=new ModelMap();
			try{
				HttpSession session=request.getSession();
				if(session.getAttribute("userRole")!="Admin")
				{
					return new ModelAndView("redirect:/login.html");
				}
				SliderModel slider_model=new SliderModel();
				slider_model.setSlider_id(null);
				slider_model.setSlider_type(s_bean.getSlider_type());
				String imageName="";
				if (s_bean.getSlider_image() != "" && !s_bean.getSlider_image().equals("")) {
					String filePath = s_bean.getSlider_image();
					String[] parts = filePath.split("base64,");
					String part2 = parts[1]; // 034556
					int rand = RandomUtils.nextInt();
					imageName =  rand + ".png";
					String directory = 
							Assets.SLIDER_IMAGES_SYS + imageName;
				
					byte[] imageByteArray = Base64.decodeBase64(part2);
					// Write a image byte array into file system
					FileOutputStream imageOutFile = new FileOutputStream(
							directory);
					imageOutFile.write(imageByteArray);
					imageOutFile.close();
				} else {
					imageName = "";
				}
				slider_model.setSlider_image(imageName);
				slider_model.setSlider_image_ar("");
				schoolservice.addSlider(slider_model);
				
				model.addAttribute("success", "Slider added successfully");
				model.addAttribute("heading", "Add Slider");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/add_slider",model);
		}
		/**
		 * Function for edit Slider
		 **/
		@RequestMapping(value = "admin/editSlider", method = RequestMethod.GET)
		public ModelAndView editSlider(
				@ModelAttribute("command") StudentBean students,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int slider_id=Integer.parseInt(new String(decodedBytes));
			SliderModel slider_model= schoolservice.getSliderById(slider_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (slider_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.put("slider_model", slider_model);
				return new ModelAndView("SuperAdmin/edit_slider", model);
			} else {
				return new ModelAndView("redirect:manage_slider");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_slider");
			}
			

		}
		/**
		 * Function for update slider
		 **/
		@RequestMapping(value = "admin/editSlider", method = RequestMethod.POST)
		public ModelAndView editSlider(
				@ModelAttribute("command") SliderBean s_bean,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int slider_id=Integer.parseInt(new String(decodedBytes));
			SliderModel slider_model= schoolservice.getSliderById(slider_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (slider_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				String imageName="";
				if(s_bean.getSlider_image()!=null)
				{
			
				if (s_bean.getSlider_image() != "" && !s_bean.getSlider_image().equals("")) {
					String filePath = s_bean.getSlider_image();
					String[] parts = filePath.split("base64,");
					String part2 = parts[1]; // 034556
					int rand = RandomUtils.nextInt();
					imageName =  rand + ".png";
					String directory =Assets.SLIDER_IMAGES_SYS + imageName;
				
					byte[] imageByteArray = Base64.decodeBase64(part2);
					// Write a image byte array into file system
					FileOutputStream imageOutFile = new FileOutputStream(
							directory);
					imageOutFile.write(imageByteArray);
					imageOutFile.close();
				}else
				{
					imageName=slider_model.getSlider_image();
				}
				}else
				{
					imageName=slider_model.getSlider_image();
				}
				SliderModel s_model=new SliderModel();
				s_model.setSlider_image(imageName);
				s_model.setSlider_type(s_bean.getSlider_type());
				schoolservice.updateSliderById(slider_id,s_model);
				slider_model= schoolservice.getSliderById(slider_id);
				model.put("slider_model", slider_model);
				model.put("success", "Slider updated successfully");
				return new ModelAndView("SuperAdmin/edit_slider", model);
			} else {
				return new ModelAndView("redirect:manage_slider");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_slider");
			}
			

		}
		@RequestMapping(value = "admin/deleteSlider", method = RequestMethod.GET)
		public ModelAndView deleteSlider(
				@ModelAttribute("command") SliderBean slider_bean,
				BindingResult result, ModelMap modelH) {
			schoolservice.deleteSlider(slider_bean.getSlider_id());
			return new ModelAndView("redirect:manage_slider");
		}
		/**
		 *@access public
		 *Method for manage homepage section-2 
		 **/
		@RequestMapping(value="admin/home_section_two", method = RequestMethod.GET)
		public ModelAndView home_section_two(@ModelAttribute("command") ModelMap model)
		{
			return new ModelAndView("SuperAdmin/homepage_section_two");
		}
		/**
		 *@access public
		 *Method for manage homepage section-2 
		 **/
		@RequestMapping(value="admin/manage_home_section_two", method = RequestMethod.GET)
		public ModelAndView manage_home_section_two(@ModelAttribute("command") ModelMap model,HttpServletRequest request)
		{
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("pages", ControllerHelper.iterateAllPage(schoolservice.getAllPage(0)));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_home_section_two",model);
		}
		
//		
		/**
		 * Function for edit Slider
		 **/
		@RequestMapping(value = "admin/editPage", method = RequestMethod.GET)
		public ModelAndView editPage(
				@ModelAttribute("command") StudentBean students,
				BindingResult result, HttpServletRequest request) {
			
			try{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int page_id=Integer.parseInt(new String(decodedBytes));
			PageModel page_model= schoolservice.getPageById(page_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (page_model != null) {
				
				model.put("content", page_model);
				return new ModelAndView("SuperAdmin/homepage_section_two", model);
			} else {
				return new ModelAndView("redirect:manage_home_section_two");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_home_section_two");
			}
			

		}
		/**
		 * Function for update slider
		 **/
		@RequestMapping(value = "admin/editPage", method = RequestMethod.POST)
		public ModelAndView editPage(
				@ModelAttribute("command") PageBean s_bean,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int page_id=Integer.parseInt(new String(decodedBytes));
			PageModel page_model= schoolservice.getPageById(page_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (page_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				PageModel p_model=new PageModel();
				p_model.setPage_desc(s_bean.getPage_desc());
				p_model.setPage_name(s_bean.getPage_name());
				p_model.setPage_title(s_bean.getPage_name());
				schoolservice.updatePageById(page_id,p_model);
				page_model= schoolservice.getPageById(page_id);
				model.put("content", page_model);
				model.put("success", "Page updated successfully");
				return new ModelAndView("SuperAdmin/homepage_section_two", model);
			} else {
				return new ModelAndView("redirect:manage_home_section_two");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_home_section_two");
			}
			

		}
		
		/**
		 *@access public
		 *Method for manage homepage section-2 
		 **/
		@RequestMapping(value="admin/manage_home_section_three", method = RequestMethod.GET)
		public ModelAndView manage_home_section_three(@ModelAttribute("command") ModelMap model,HttpServletRequest request)
		{
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("pages", ControllerHelper.iterateAllPage(schoolservice.getAllPage(1)));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_home_section_three",model);
		}
		/**
		 * Function for edit Slider
		 **/
		@RequestMapping(value = "admin/editPageSectionThree", method = RequestMethod.GET)
		public ModelAndView editPageSectionThree(
				@ModelAttribute("command") StudentBean students,
				BindingResult result, HttpServletRequest request) {
			
			try{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int page_id=Integer.parseInt(new String(decodedBytes));
			PageModel page_model= schoolservice.getPageById(page_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (page_model != null) {
				
				model.put("content", page_model);
				return new ModelAndView("SuperAdmin/homepage_section_three", model);
			} else {
				return new ModelAndView("redirect:manage_home_section_three");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_home_section_three");
			}
			

		}
		
		/**
		 * Function for update slider
		 **/
		@RequestMapping(value = "admin/editPageSectionThree", method = RequestMethod.POST)
		public ModelAndView editPageSectionThree(
				@ModelAttribute("command") PageBean s_bean,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int page_id=Integer.parseInt(new String(decodedBytes));
			PageModel page_model= schoolservice.getPageById(page_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (page_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				String imageName="";
				if (s_bean.getPage_title() != "" && !s_bean.getPage_title().equals("")) {
					String filePath = s_bean.getPage_title();
					String[] parts = filePath.split("base64,");
					String part2 = parts[1]; // 034556
					int rand = RandomUtils.nextInt();
					imageName =  rand + ".png";
					String directory = Assets.SLIDER_IMAGES_SYS + imageName ;
				
					byte[] imageByteArray = Base64.decodeBase64(part2);
					// Write a image byte array into file system
					FileOutputStream imageOutFile = new FileOutputStream(
							directory);
					imageOutFile.write(imageByteArray);
					imageOutFile.close();
				}else
				{
					imageName=page_model.getPage_title();
				}
				PageModel p_model=new PageModel();
				p_model.setPage_title(imageName);
				p_model.setPage_desc(s_bean.getPage_desc());
				p_model.setPage_name(s_bean.getPage_name());
				schoolservice.updatePageById(page_id,p_model);
				page_model= schoolservice.getPageById(page_id);
				model.put("content", page_model);
				model.put("success", "Page updated successfully");
				
				return new ModelAndView("SuperAdmin/homepage_section_three", model);
			} else {
				return new ModelAndView("redirect:manage_home_section_three");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_home_section_three");
			}
			

		}
		
		/**
		 * @access public
		 * Function for edit Page
		 **/
		@RequestMapping(value = "admin/manage_home_section_four", method = RequestMethod.GET)
		public ModelAndView manage_home_section_four(
				@ModelAttribute("command") StudentBean students,
				BindingResult result, HttpServletRequest request) {
			
			try{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
			PageModel page_model= schoolservice.getPageById(5);
			Map<String, Object> model = new HashMap<String, Object>();
			if (page_model != null) {
				
				model.put("content", page_model);
				return new ModelAndView("SuperAdmin/homepage_section_four", model);
			} else {
				return new ModelAndView("redirect:manage_home_section_four");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_home_section_four");
			}
			

		}
		
		/**
		 * Function for update Page
		 **/
		@RequestMapping(value = "admin/manage_home_section_four", method = RequestMethod.POST)
		public ModelAndView manage_home_section_four(
				@ModelAttribute("command") PageBean s_bean,
				BindingResult result, HttpServletRequest request) {
			
			try{
			int page_id=5;
			PageModel page_model= schoolservice.getPageById(page_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (page_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				PageModel p_model=new PageModel();
				p_model.setPage_desc("");
				p_model.setPage_name(s_bean.getPage_name());
				p_model.setPage_title("");
				schoolservice.updatePageById(page_id,p_model);
				page_model= schoolservice.getPageById(page_id);
				model.put("content", page_model);
				model.put("success", "Page updated successfully");
				return new ModelAndView("SuperAdmin/homepage_section_four", model);
			} else {
				return new ModelAndView("redirect:manage_home_section_four");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_home_section_four");
			}
			

		}
		/**
		 *@access public
		 *Method for manage homepage section-2 
		 **/
		@RequestMapping(value="admin/manage_home_section_five", method = RequestMethod.GET)
		public ModelAndView manage_home_section_five(@ModelAttribute("command") ModelMap model,HttpServletRequest request)
		{
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("pages", ControllerHelper.iterateAllPage(schoolservice.getAllPage(3)));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_home_section_two",model);
		}
		/**
		 * Function for edit Slider
		 **/
		@RequestMapping(value = "admin/editPageSectionFive", method = RequestMethod.GET)
		public ModelAndView editPageSectionFive(
				@ModelAttribute("command") StudentBean students,
				BindingResult result, HttpServletRequest request) {
			
			try{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int page_id=Integer.parseInt(new String(decodedBytes));
			PageModel page_model= schoolservice.getPageById(page_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (page_model != null) {
				
				model.put("content", page_model);
				return new ModelAndView("SuperAdmin/homepage_section_two", model);
			} else {
				return new ModelAndView("redirect:manage_home_section_five");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_home_section_five");
			}
			

		}
		/**
		 * Function for update slider
		 **/
		@RequestMapping(value = "admin/editPageSectionFive", method = RequestMethod.POST)
		public ModelAndView editPageSectionFive(
				@ModelAttribute("command") PageBean s_bean,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int page_id=Integer.parseInt(new String(decodedBytes));
			PageModel page_model= schoolservice.getPageById(page_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (page_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				PageModel p_model=new PageModel();
				p_model.setPage_desc(s_bean.getPage_desc());
				p_model.setPage_name(s_bean.getPage_name());
				p_model.setPage_title(s_bean.getPage_name());
				schoolservice.updatePageById(page_id,p_model);
				page_model= schoolservice.getPageById(page_id);
				model.put("content", page_model);
				model.put("success", "Page updated successfully");
				return new ModelAndView("SuperAdmin/homepage_section_two", model);
			} else {
				return new ModelAndView("redirect:manage_home_section_two");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_home_section_two");
			}
			

		}
		/**
		 *@access public
		 *Method for manage features 
		 **/
		@RequestMapping(value="admin/manageFeatures", method = RequestMethod.GET)
		public ModelAndView manageFeatures(@ModelAttribute("command") ModelMap model,HttpServletRequest request)
		{
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("features", ControllerHelper. iterateAllFeatures(schoolservice.getAllFeatures()));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_features",model);
		}

		
		/**
		 * @access public
		 * Method for add slider 
		 * */		
		@RequestMapping(value="admin/addFeature",method=RequestMethod.GET)
		public ModelAndView addFeature(@ModelAttribute("command") SliderBean slider_bean,BindingResult result,ModelMap model,HttpServletRequest request)
		{
			try{
				HttpSession session=request.getSession();
				if(session.getAttribute("userRole")!="Admin") {
					return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("heading", "Add Feature");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/add_feature");
		}
		
		/**
		 * @access public
		 *Method for save slider image 
		 **/
		@RequestMapping(value="admin/addFeature", method=RequestMethod.POST)
		public ModelAndView addFeature(@ModelAttribute("command") FeaturesBean s_bean,BindingResult result,HttpServletRequest request)
		{
			ModelMap model=new ModelMap();
			try{
				HttpSession session=request.getSession();
				if(session.getAttribute("userRole")!="Admin")
				{
					return new ModelAndView("redirect:/login.html");
				}
				FeaturesModel feature_model=new FeaturesModel();
				feature_model.setFeatures_id(null);
				feature_model.setTitle(s_bean.getTitle());
				feature_model.setCategory_type(s_bean.getCategory_type());
				feature_model.setContent(s_bean.getContent());
				
				feature_model.setTitle_ar(s_bean.getTitle_ar());
				feature_model.setContent_ar(s_bean.getContent_ar());

				
				String imageName="";
				if (s_bean.getImage_path() != "" && !s_bean.getImage_path().equals("")) {
					String filePath = s_bean.getImage_path();
					String[] parts = filePath.split("base64,");
					String part2 = parts[1]; // 034556
					int rand = RandomUtils.nextInt();
					imageName =  rand + ".png";
					String directory =  Assets.FEATURES_IMAGE_SYS + imageName ;
				
					byte[] imageByteArray = Base64.decodeBase64(part2);
					// Write a image byte array into file system
					FileOutputStream imageOutFile = new FileOutputStream(
							directory);
					imageOutFile.write(imageByteArray);
					imageOutFile.close();
				} else {
					imageName = "";
				}
				feature_model.setImage_path(imageName);
				feature_model.setImage_path_ar(imageName);

				schoolservice.addFeature(feature_model);
				
				model.addAttribute("success", "Feature added successfully");
				model.addAttribute("heading", "Add Feature");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/add_feature",model);
		}
		
		/**
		 *@access public 
		 * Method for update feature
		 **/
		@RequestMapping(value = "admin/editFeature", method = RequestMethod.GET)
		public ModelAndView editFeature(
				@ModelAttribute("command") StudentBean students,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int feature_id=Integer.parseInt(new String(decodedBytes));
			FeaturesModel feature_model= schoolservice.getFeatureById(feature_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (feature_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.put("feature_model", feature_model);
				return new ModelAndView("SuperAdmin/edit_feature", model);
			} else {
				return new ModelAndView("redirect:manageFeatures");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manageFeatures");
			}
			

		}
		
		/**
		 * Function for update slider
		 **/
		@RequestMapping(value = "admin/editFeature", method = RequestMethod.POST)
		public ModelAndView editFeature(
				@ModelAttribute("command") FeaturesBean s_bean,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int feature_id=Integer.parseInt(new String(decodedBytes));
			System.out.println(feature_id);
			FeaturesModel feature_model1= schoolservice.getFeatureById(feature_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (feature_model1 != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				FeaturesModel feature_model=new FeaturesModel();
				feature_model.setTitle(s_bean.getTitle());
				feature_model.setCategory_type(s_bean.getCategory_type());
				feature_model.setContent(s_bean.getContent());
				
				feature_model.setTitle_ar(s_bean.getTitle_ar());
				feature_model.setContent_ar(s_bean.getContent_ar());

				String imageName="";
				
				if (s_bean.getImage_path()!=null &&  s_bean.getImage_path() != "" && !s_bean.getImage_path().equals("")) {
					
					String filePath = s_bean.getImage_path();
					String[] parts = filePath.split("base64,");
					String part2 = parts[1]; // 034556
					int rand = RandomUtils.nextInt();
					imageName =  rand + ".png";
					String directory =  Assets.FEATURES_IMAGE_SYS + imageName ;
				
					byte[] imageByteArray = Base64.decodeBase64(part2);
					// Write a image byte array into file system
					FileOutputStream imageOutFile = new FileOutputStream(
							directory);
					imageOutFile.write(imageByteArray);
					imageOutFile.close();
					
				
				}else {
					imageName = feature_model1.getImage_path();
				}
				System.out.println(s_bean.getImage_path()+"Image Path="+imageName);
				feature_model.setImage_path(imageName); 
				
				schoolservice.updateFeatureById(feature_id,feature_model);
				feature_model1= schoolservice.getFeatureById(feature_id);
				model.put("success", "Feature updated successfully");
				model.put("feature_model", feature_model1);
				return new ModelAndView("SuperAdmin/edit_feature", model);
			} else {
				return new ModelAndView("redirect:manageFeatures");
			}
			}catch(Exception e)
			{
				e.printStackTrace();
				return new ModelAndView("redirect:manageFeatures");
			}
			

		}
		/**
		 *@access public
		 *Method for delete features 
		 **/
		@RequestMapping(value = "admin/deleteFeature", method = RequestMethod.GET)
		public ModelAndView deleteFeature(
				@ModelAttribute("command") FeaturesBean feature_bean,
				BindingResult result, ModelMap modelH) {
			schoolservice.deleteFeature(feature_bean.getFeatures_id());
			return new ModelAndView("redirect:manageFeatures");
		}
		/**
		 *@access public
		 *Method for manage faq 
		 **/
		@RequestMapping(value="admin/manageFaq", method = RequestMethod.GET)
		public ModelAndView manageFaq(@ModelAttribute("command") ModelMap model,HttpServletRequest request)
		{
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("faq",ControllerHelper. iterateAllFaq(schoolservice.getAllFaq()));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_faq",model);
		}
		
		/**
		 * @access public
		 * Method for add faq 
		 * */		
		@RequestMapping(value="admin/addFaq",method=RequestMethod.GET)
		public ModelAndView addFaq(@ModelAttribute("command") SliderBean slider_bean,BindingResult result,ModelMap model,HttpServletRequest request)
		{
			try{
				HttpSession session=request.getSession();
				if(session.getAttribute("userRole")!="Admin")
				{
					return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("heading", "Add FAQ");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/add_faq",model);
		}
		/**
		 * @access public
		 *Method for faq
		 **/
		@RequestMapping(value="admin/addFaq", method=RequestMethod.POST)
		public ModelAndView addFaq(@ModelAttribute("command") FaqBean s_bean,BindingResult result,HttpServletRequest request)
		{
			ModelMap model=new ModelMap();
			try{
				HttpSession session=request.getSession();
				if(session.getAttribute("userRole")!="Admin")
				{
					return new ModelAndView("redirect:/login.html");
				}
				FaqModel faq_model=new FaqModel();
				faq_model.setFaq_id(null);
				faq_model.setCategory(s_bean.getCategory());
				faq_model.setQuestion(s_bean.getQuestion());
				faq_model.setAnswer(s_bean.getAnswer());
				faq_model.setQuestion_ar("");
				faq_model.setAnswer_ar("");
				faq_model.setLang(0);
				schoolservice.addFaq(faq_model);
				
				model.addAttribute("success", "Faq added successfully");
				model.addAttribute("heading", "Add Faq");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/add_faq",model);
		}
		/**
		 * Function for edit Slider
		 **/
		@RequestMapping(value = "admin/editFaq", method = RequestMethod.GET)
		public ModelAndView editFaq(
				@ModelAttribute("command") StudentBean students,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int faq_id=Integer.parseInt(new String(decodedBytes));
			FaqModel faq_model= schoolservice.getFaqById(faq_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (faq_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.put("faq", faq_model);
				return new ModelAndView("SuperAdmin/edit_faq", model);
			} else {
				return new ModelAndView("redirect:manageFaq");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manageFaq");
			}
			

		}
		/**
		 * Function for update slider
		 **/
		@RequestMapping(value = "admin/editFaq", method = RequestMethod.POST)
		public ModelAndView editFaq(
				@ModelAttribute("command") FaqBean s_bean,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int faq_id=Integer.parseInt(new String(decodedBytes));
			FaqModel faq_model= schoolservice.getFaqById(faq_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (faq_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				FaqModel faq_model1=new FaqModel();
				faq_model1.setCategory(s_bean.getCategory());
				faq_model1.setQuestion(s_bean.getQuestion());
				faq_model1.setAnswer(s_bean.getAnswer());
				schoolservice.updateFaqById(faq_id,faq_model1);
				faq_model= schoolservice.getFaqById(faq_id);
				model.put("success", "Faq updated successfully");
				model.put("faq", faq_model);
				return new ModelAndView("SuperAdmin/edit_faq", model);
			} else {
				return new ModelAndView("redirect:manageFaq");
			}
			}catch(Exception e)
			{
				return new ModelAndView("redirect:manageFaq");
			}
			

		}
		
		/**
		 *@access public
		 *Method for delete Faq 
		 **/
		@RequestMapping(value = "admin/deleteFaq", method = RequestMethod.GET)
		public ModelAndView deleteFeature(
				@ModelAttribute("command") FaqBean faq_bean,
				BindingResult result, ModelMap modelH) {
			schoolservice.deleteFaq(faq_bean.getFaq_id());
			return new ModelAndView("redirect:manageFaq");
		}
		/**
		 *@access public
		 *Method for set and redirect for language
		 **/
		@RequestMapping(value = "admin/langSwitcher", method = RequestMethod.GET)
		public ModelAndView langSwitcher(@ModelAttribute("command") StudentBean students,
				BindingResult result, HttpServletRequest request) {
				try{
					String q=request.getParameter("q");
					int u=Integer.parseInt(request.getParameter("u"));
					int ns=0;
					if(request.getParameter("ns")!=null && !request.getParameter("ns").equals(""))
					{
						ns=Integer.parseInt(request.getParameter("ns"));
					}
					
					LoginModel found = schoolservice.getUserById(u);
					if(found!=null){
						HttpSession session=request.getSession();
						session.setAttribute("f_name", found.getFirst_name());
						session.setAttribute("l_name", found.getLast_name());
						session.setAttribute("family_name", found.getFamily_name());
						session.setAttribute("userName", found.getUser_name());
						session.setAttribute("userEmail", found.getUser_email());
						session.setAttribute("userRole", "Admin");
						session.setAttribute("permission", found.getPermission());
						session.setAttribute("user_id", found.getUser_id());
						if(request.getParameter("ns")!=null && !request.getParameter("ns").equals(""))
						{
							session.setAttribute("new_school_id",ns);
						}
						
						return new ModelAndView("redirect:/"+q);
					}else
					{
						return new ModelAndView("redirect:login.html");
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				//return new ModelAndView("redirect:manageFaq");
				return null;
		}
		
		
		/**
		 *@access public
		 *Method for manage faq 
		 **/
		@RequestMapping(value="admin/manageSubscrubers", method = RequestMethod.GET)
		public ModelAndView manageSubscrubers(@ModelAttribute("command") ModelMap model,HttpServletRequest request)
		{
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("faq", ControllerHelper. iterateAllSubscribers(schoolservice.getAllSubscribers()));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_subscribers",model);
		}
		
		/**
		 *@access public
		 *Method for delete Subscribers 
		 **/
		@RequestMapping(value = "admin/deleteSubscriber", method = RequestMethod.GET)
		public ModelAndView deleteSubscriber(
				@ModelAttribute("command") SubscriberBean s_bean,
				BindingResult result, ModelMap modelH) {
			schoolservice.deleteSubscriber(s_bean.getSub_id());
			return new ModelAndView("redirect:manageSubscrubers");
		}
		
		/**
		 *@access public
		 *Method for admin email setup 
		 **/
		@RequestMapping(value = "admin/adminSetup", method = RequestMethod.GET)
		public ModelAndView adminSetup(
				@ModelAttribute("command") AdminSetupBean s_bean,
				BindingResult result, ModelMap modelH,HttpServletRequest request) {
			try{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
			AdminSetupModel asm=new AdminSetupModel();
			asm=schoolservice.adminSetup();
			modelH.put("details", asm);
			//return new ModelAndView("SuperAdmin/admin_setup",modelH);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/admin_setup",modelH);
		}
		
		/**
		 *@access public
		 *Method for admin email setup 
		 **/
		@RequestMapping(value = "admin/adminSetup", method = RequestMethod.POST)
		public ModelAndView adminSetup(
				@ModelAttribute("command") AdminSetupBean s_bean,
				BindingResult result,HttpServletRequest request ,ModelMap modelH) {
			try{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
			AdminSetupModel asm=new AdminSetupModel();
			asm.setEmail_id(s_bean.getEmail_id());
			asm.setPassword(s_bean.getPassword());
			schoolservice.updateAdminSetup(asm);
			//return new ModelAndView("SuperAdmin/admin_setup",modelH);
			
			
			AdminSetupModel asm1=new AdminSetupModel();
			asm1=schoolservice.adminSetup();
			modelH.put("details", asm1);
			modelH.put("success", "Setting has been updated");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/admin_setup",modelH);
		}
		/**
		 * Function for edit Slider
		 **/
		@RequestMapping(value = "admin/emailTemplate", method = RequestMethod.GET)
		public ModelAndView emailTemplate(
				@ModelAttribute("command") EmailTemplateBean students,
				BindingResult result, HttpServletRequest request) {
			
			try{
			int temp_id=1;
			EmailTemplateModel email_model= schoolservice.getEmailTemaplteById(temp_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (email_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.put("templates", email_model);
				return new ModelAndView("SuperAdmin/edit_email_template", model);
			} else {
				return new ModelAndView("redirect:manageSubscrubers");
			}
			}catch(Exception e)
			{	
				return new ModelAndView("redirect:manageSubscrubers");
			}
			

		}
		/**
		 * Function for update slider
		 **/
		@RequestMapping(value = "admin/emailTemplate", method = RequestMethod.POST)
		public ModelAndView emailTemplate(
				@ModelAttribute("command") EmailTemplateBean email_bean,
				BindingResult result, HttpServletRequest request,ModelMap model2){
			
			try{
			int temp_id=1;
			EmailTemplateModel email_temp= schoolservice.getEmailTemaplteById(temp_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (email_temp!=null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				EmailTemplateModel email_model_save=new EmailTemplateModel();
				email_model_save.setDescription(email_bean.getDesc());
				schoolservice.updateEmailTemplate(email_model_save, temp_id);
				EmailTemplateModel email_model= schoolservice.getEmailTemaplteById(temp_id);
				model.put("success", "Template updated successfully");
				model.put("templates", email_model);
				return new ModelAndView("SuperAdmin/edit_email_template", model);
			} else {
				return new ModelAndView("redirect:manageSubscrubers");
			}
			}catch(Exception e)
			{
				e.printStackTrace();
				return new ModelAndView("redirect:manageSubscrubers");
			}
			

		}
		/**
		 * Function for edit Slider
		 **/
		@RequestMapping(value = "admin/sendEmailTemplates", method = RequestMethod.POST)
		public ModelAndView sendEmailTemplates(
				@ModelAttribute("command") EmailTemplateBean email_bean,
				BindingResult result, HttpServletRequest request,ModelMap model) {
			try{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				List<String> items = Arrays.asList(email_bean.getDesc().split("\\s*,\\s*"));
				for(String a : items)
				{	
						SubscriberModel s_model = schoolservice.getSubscriberById(Integer.parseInt(a));
						
						ApplicationMailer send_email=new ApplicationMailer();
						int temp_id=1;
						EmailTemplateModel email_temp= schoolservice.getEmailTemaplteById(temp_id);
						String msg="";
						msg +=email_temp.getDescription();
						
						AdminSetupModel asm=new AdminSetupModel();
						asm=schoolservice.adminSetup();
					//	send_email.sendEmail(asm.getEmail_id(), msg, "Tracking Bus",asm.getPassword());
						send_email.sendEmailPromotional(s_model.getEmail(), msg, "Tracking Bus", asm.getPassword(), asm.getEmail_id());
						//schoolservice.addContactDetails(c_model);
						model.addAttribute("faq",ControllerHelper. iterateAllSubscribers(schoolservice.getAllSubscribers()));
						model.addAttribute("success", "Email sent successfully");
				}
				//return new ModelAndView("redirect:manageSubscrubers");
				return new ModelAndView("SuperAdmin/manage_subscribers",model);
			}catch(Exception e)
			{	
				return new ModelAndView("redirect:manageSubscrubers");
			}
			

		}
		
		/**
		 * Function for edit Slider
		 **/
		@RequestMapping(value = "admin/sendEmailTemplates", method = RequestMethod.GET)
		public ModelAndView sendEmailTemplates(@ModelAttribute("command") ModelMap model,HttpServletRequest request) {
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("faq",ControllerHelper. iterateAllSubscribers(schoolservice.getAllSubscribers()));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_subscribers",model);
			

		}
		
		
		/**
		 *Method for manage contact page category 
		 **/
		@RequestMapping(value="admin/manage_contact_category",method=RequestMethod.GET)
		public ModelAndView manage_contact_category(@ModelAttribute("command") ModelMap model,HttpServletRequest request)
		{
			try
			{
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("categories", ControllerHelper. iterateAllCategory(schoolservice.getAllContactCategory()));
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/manage_category",model);
		}
		
		/**
		 * @access public
		 * Method for add slider 
		 * */		
		@RequestMapping(value="admin/addCategory",method=RequestMethod.GET)
		public ModelAndView addCategory(@ModelAttribute("command") ModelMap model,BindingResult result,HttpServletRequest request)
		{
			try{
				HttpSession session=request.getSession();
				if(session.getAttribute("userRole")!="Admin")
				{
					return new ModelAndView("redirect:/login.html");
				}
				model.addAttribute("heading", "Add Category");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/add_category");
		}
		
		/**
		 * @access public
		 *Method for save slider image 
		 **/
		@RequestMapping(value="admin/addCategory", method=RequestMethod.POST)
		public ModelAndView addCategory(@ModelAttribute("command") CategoryContactBean s_bean,BindingResult result,HttpServletRequest request)
		{
			ModelMap model=new ModelMap();
			try{
				HttpSession session=request.getSession();
				if(session.getAttribute("userRole")!="Admin")
				{
					return new ModelAndView("redirect:/login.html");
				}
				CategoryContactModel category_model=new CategoryContactModel();
				category_model.setC_cat_id(null);
				category_model.setCategory_en(s_bean.getCategory_en());
				category_model.setCategory_ar("");
				
				schoolservice.addCategory(category_model);
				
				model.addAttribute("success", "Category added successfully");
				model.addAttribute("heading", "Add Category");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ModelAndView("SuperAdmin/add_category",model);
		}
		
		/**
		 * Function for edit Category
		 **/
		@RequestMapping(value = "admin/editCategory", method = RequestMethod.GET)
		public ModelAndView editCategory(
				@ModelAttribute("command") CategoryContactBean category_bean,
				BindingResult result, HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int c_cat_id=Integer.parseInt(new String(decodedBytes));
			CategoryContactModel category_model= schoolservice.getCategoryById(c_cat_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (category_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				model.put("category_model", category_model);
				return new ModelAndView("SuperAdmin/edit_category", model);
			} else {
				return new ModelAndView("redirect:manage_contact_category");
			}
			}catch(Exception e)
			{	return new ModelAndView("redirect:manage_contact_category");
			}
			

		}
		
		/**
		 * Function for update Category
		 **/
		@RequestMapping(value = "admin/editCategory", method = RequestMethod.POST)
		public ModelAndView editCategory(
				@ModelAttribute("command") CategoryContactBean category_bean,
				BindingResult result, ModelMap modelM,HttpServletRequest request) {
			
			try{
			String q=request.getParameter("q");
			byte[] decodedBytes = Base64.decodeBase64(""+q+"");
			int c_cat_id=Integer.parseInt(new String(decodedBytes));
			CategoryContactModel category_model= schoolservice.getCategoryById(c_cat_id);
			Map<String, Object> model = new HashMap<String, Object>();
			if (category_model != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("userRole")!= "Admin")
				{
				 return new ModelAndView("redirect:/login.html");
				}
				
				CategoryContactModel s_model=new CategoryContactModel();
				
				s_model.setCategory_en(category_bean.getCategory_en());
				
				schoolservice.updateCategoryById(c_cat_id,s_model);
				category_model= schoolservice.getCategoryById(c_cat_id);
				model.put("category_model", category_model);
				
				model.put("success", "Category updated successfully");
				return new ModelAndView("SuperAdmin/edit_category", model);
			} else {
				
				return new ModelAndView("redirect:manage_contact_category");
			}
			}catch(Exception e)
			{	
			 
				return new ModelAndView("redirect:manage_contact_category");
			}
			

		}
		
		@RequestMapping(value = "admin/deleteCategory", method = RequestMethod.GET)
		public ModelAndView deleteCategory(
				@ModelAttribute("command") CategoryContactBean category_bean,
				BindingResult result, ModelMap modelH) {
			schoolservice.deleteCategory(category_bean.getC_cat_id());
			return new ModelAndView("redirect:manage_contact_category");
		}
		
		/**
		 * Function for About us page content
		 **/
		@RequestMapping(value = "admin/manage_contact_page", method = RequestMethod.GET)
		public ModelAndView manage_contact_page(
				@ModelAttribute("command") StudentBean studentbean,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			
			ContactContentModel page_content=schoolservice.getContactContent(1);
			model.addAttribute("content", page_content);
			model.addAttribute("heading", "Contact us");
			return new ModelAndView("SuperAdmin/contact_us", model);
		}
		/**
		 * Function for About us page content using post
		 **/
		@RequestMapping(value = "admin/manage_contact_page", method = RequestMethod.POST)
		public ModelAndView manage_contact_page(
				@ModelAttribute("command") ContactContentBean contact_bean,
				BindingResult result, ModelMap model, HttpServletRequest request,
				PageContentBean page_bean) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			ContactContentModel page_model = new ContactContentModel();
			page_model.setC_id(null);
			page_model.setAddress(contact_bean.getAddress());
			page_model.setEmail(contact_bean.getEmail());
			page_model.setPhone_number(contact_bean.getPhone_number());
			page_model.setWebsite(contact_bean.getWebsite());
			page_model.setLocation(contact_bean.getLocation());
			schoolservice.updateContactContent(page_model, 1);
			ContactContentModel page_content=schoolservice.getContactContent(1);
			model.addAttribute("content", page_content);
			model.addAttribute("heading", "Contact us");
			model.addAttribute("success", "Page updated successfully");
			return new ModelAndView("SuperAdmin/contact_us", model);
		}
		
		/**
		 * Function for About us page content
		 **/
		@RequestMapping(value = "admin/manage_video", method = RequestMethod.GET)
		public ModelAndView manage_video(
				@ModelAttribute("command") StudentBean studentbean,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			PageContentModel page_content=schoolservice.getAboutUs(3);
			model.addAttribute("content", page_content);
			model.addAttribute("heading", "Homepage video");
			return new ModelAndView("SuperAdmin/manage_video", model);
		}
		/**
		 * Function for About us page content using post
		 **/
		@RequestMapping(value = "admin/manage_video", method = RequestMethod.POST)
		public ModelAndView manage_video(
				@ModelAttribute("command") StudentBean studentbean,
				BindingResult result, ModelMap model, HttpServletRequest request,
				PageContentBean page_bean) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			PageContentModel page_model = new PageContentModel();
			page_model.setP_id(null);
			page_model.setP_name(page_bean.getP_name());
			page_model.setHeading1("");
			page_model.setHeading2("");
			page_model.setHeading3("");
			page_model.setDescription1("");
			page_model.setDescription2("");
			page_model.setDescription3("");
			page_model.setHeading4("");
			page_model.setHeading5("");
			page_model.setDescription4("");
			page_model.setDescription5("");
			schoolservice.updatePageContent(page_model, 3);
			PageContentModel page_content=schoolservice.getAboutUs(3);
			model.addAttribute("content", page_content);
			model.addAttribute("heading", "Homepage video");
			model.addAttribute("success", "Video updated successfully");
			return new ModelAndView("SuperAdmin/manage_video", model);
		}
		/**
		 * Function for About us page content
		 **/
		@RequestMapping(value = "admin/social_link", method = RequestMethod.GET)
		public ModelAndView social_link(
				@ModelAttribute("command") StudentBean studentbean,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			PageContentModel page_content=schoolservice.getAboutUs(5);
			model.addAttribute("content", page_content);
			model.addAttribute("heading", "Social Links");
			return new ModelAndView("SuperAdmin/social_link", model);
		}
		/**
		 * Function for About us page content using post
		 **/
		@RequestMapping(value = "admin/social_link", method = RequestMethod.POST)
		public ModelAndView social_link(
				@ModelAttribute("command") StudentBean studentbean,
				BindingResult result, ModelMap model, HttpServletRequest request,
				PageContentBean page_bean) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			PageContentModel page_model = new PageContentModel();
			page_model.setP_id(null);
			page_model.setP_name(page_bean.getP_name());
			page_model.setHeading1(page_bean.getHeading1());
			page_model.setHeading2(page_bean.getHeading2());
			page_model.setHeading3("");
			page_model.setDescription1("");
			page_model.setDescription2("");
			page_model.setDescription3("");
			page_model.setHeading4("");
			page_model.setHeading5("");
			page_model.setDescription4("");
			page_model.setDescription5("");
			schoolservice.updatePageContent(page_model, 5);
			PageContentModel page_content=schoolservice.getAboutUs(5);
			model.addAttribute("content", page_content);
			model.addAttribute("heading", "Social link");
			model.addAttribute("success", "Social link updated successfully");
			return new ModelAndView("SuperAdmin/social_link", model);
		}
		
		/**
		 * Function for add play icon
		 **/
		@RequestMapping(value = "admin/play_icon", method = RequestMethod.GET)
		public ModelAndView play_icon(
				@ModelAttribute("command") StudentBean studentbean,
				BindingResult result, ModelMap model, HttpServletRequest request) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			PageContentModel page_content=schoolservice.getAboutUs(6);
			model.addAttribute("content", page_content);
			model.addAttribute("heading", "Update play icon");
			return new ModelAndView("SuperAdmin/play_icon", model);
		}
		/**
		 * Function for About us page content using post
		 **/
		@RequestMapping(value = "admin/play_icon", method = RequestMethod.POST)
		public ModelAndView play_icon(
				@ModelAttribute("command") StudentBean studentbean,
				BindingResult result, ModelMap model, HttpServletRequest request,
				PageContentBean page_bean) {
			HttpSession session = request.getSession();
			if (session.getAttribute("userRole")!= "Admin")
			{
			 return new ModelAndView("redirect:/login.html");
			}
			PageContentModel page_model = new PageContentModel();
			page_model.setP_id(null);
			page_model.setP_name(page_bean.getP_name());
			page_model.setHeading1(page_bean.getHeading1());
			page_model.setHeading2(page_bean.getHeading2());
			page_model.setHeading3(page_bean.getHeading3());
			page_model.setDescription1("");
			page_model.setDescription2("");
			page_model.setDescription3("");
			page_model.setHeading4(page_bean.getHeading4());
			page_model.setHeading5("");
			page_model.setDescription4("");
			page_model.setDescription5("");
			schoolservice.updatePageContent(page_model, 6);
			PageContentModel page_content=schoolservice.getAboutUs(6);
			model.addAttribute("content", page_content);
			model.addAttribute("heading", "Play Icon");
			model.addAttribute("success", "Play Icon updated successfully");
			return new ModelAndView("SuperAdmin/play_icon", model);
		}
		
}

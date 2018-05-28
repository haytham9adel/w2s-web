package com.trackingbus.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.math.RandomUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import resources.Assets;

import com.trackingbus.bean.AttendanceBean;
import com.trackingbus.bean.CategoryContactBean;
import com.trackingbus.bean.CityBean;
import com.trackingbus.bean.CountryBean;
import com.trackingbus.bean.DriverAttendanceBean;
import com.trackingbus.bean.DriverBean;
import com.trackingbus.bean.DriverDocBean;
import com.trackingbus.bean.DriverTrack;
import com.trackingbus.bean.FaqBean;
import com.trackingbus.bean.FeaturesBean;
import com.trackingbus.bean.GoogleResponse;
import com.trackingbus.bean.HolidayBean;
import com.trackingbus.bean.HolidayDeletedBean;
import com.trackingbus.bean.LoginBean;
import com.trackingbus.bean.NationlityBean;
import com.trackingbus.bean.PageBean;
import com.trackingbus.bean.RouteBean;
import com.trackingbus.bean.RouteLatLng;
import com.trackingbus.bean.SchoolBean;
import com.trackingbus.bean.SchoolClasses;
import com.trackingbus.bean.SliderBean;
import com.trackingbus.bean.StudentBean;
import com.trackingbus.bean.SubscriberBean;
import com.trackingbus.bean.VechileBean;
import com.trackingbus.bean.VehicleDocBean;
import com.trackingbus.bean.relationshipBean;
import com.trackingbus.model.AttendanceModel;
import com.trackingbus.model.CategoryContactModel;
import com.trackingbus.model.CityModel;
import com.trackingbus.model.CountryModel;
import com.trackingbus.model.DriverAttendanceModel;
import com.trackingbus.model.DriverDocModel;
import com.trackingbus.model.DriverModel;
import com.trackingbus.model.DriverTrackModel;
import com.trackingbus.model.FaqModel;
import com.trackingbus.model.FeaturesModel;
import com.trackingbus.model.HolidayDeletedModel;
import com.trackingbus.model.HolidayModel;
import com.trackingbus.model.LoginModel;
import com.trackingbus.model.NationlityModel;
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
import com.trackingbus.service.SchoolService;
import com.trackingbus.service.StudentService;

public class ControllerHelper {

	
	public static String decode(String value) {
	    try {
			return URLDecoder.decode( value, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return value;
	}
	
	/* Method for get Student list */
	public static List<DriverBean> getDriversList(List<DriverModel> driver , SchoolService schoolservice) {

		List<DriverBean> beans = null;
		if (driver != null && !driver.isEmpty()) {
			beans = new ArrayList<DriverBean>();
			DriverBean bean = null;
			for (DriverModel st : driver) {
				bean = new DriverBean();

				SchoolModel stud = new SchoolModel();
				stud = schoolservice.getSchoolById(st.getDriver_school_id());
				bean.setSchool_name(stud.getSchool_name());
				bean.setAddress(st.getAddress());
				bean.setContact_number(st.getContact_number());
				bean.setD_email(st.getD_email());
				bean.setDriver_fname(st.getDriver_fname());
				bean.setDriver_id(st.getDriver_id());
				bean.setDriver_lname(st.getDriver_lname());
				bean.setDriver_school_id(st.getDriver_school_id());
				bean.setImage_path(st.getImage_path());
				bean.setMiddle_name(st.getMiddle_name());
				bean.setDob(st.getDob());
				bean.setNationality(st.getNationality());
				bean.setLicence_expiry(st.getLicence_expiry());
				bean.setPassword(st.getPassword());
				bean.setUsername(st.getUsername());
				bean.setBlood_group(st.getBlood_group());
				bean.setStatus(1);
				if(st.getRoute_id()!=null && st.getRoute_id()!=0)
				{
					RouteModel route_details=schoolservice.getRouteById(st.getRoute_id());
					if(route_details!=null)
					{
						bean.setRoute_name(route_details.getRoute_name());
					}else
					{
						bean.setRoute_name("N/A");
					}
				}else
				{
					bean.setRoute_name("N/A");
				}
				beans.add(bean);
			}
		} else {
			System.out.println("empty");
		}
		return beans;
	}
	
	
	/* Method for get city list by state */
	public static List<LoginBean> getParents(List<LoginModel> users) {

		List<LoginBean> beans = null;
		if (users != null && !users.isEmpty()) {
			beans = new ArrayList<LoginBean>();
			LoginBean bean = null;
			for (LoginModel user : users) {
				bean = new LoginBean();
				bean.setUser_id(user.getUser_id());
				bean.setUser_name(user.getUser_name());
				beans.add(bean);
			}
		} else {
			System.out.println("empty");
		}
		return beans;
	}
	/* Method for get country list */
	public static List<CountryBean> prepareListofBean(List<CountryModel> country) {

		List<CountryBean> beans = null;
		if (country != null && !country.isEmpty()) {
			beans = new ArrayList<CountryBean>();
			CountryBean bean = null;
			for (CountryModel countries : country) {
				bean = new CountryBean();
				bean.setC_id(countries.getC_id());
				bean.setC_name(countries.getC_name());
				bean.setC_code(countries.getC_code());
				beans.add(bean);
			}
		} else {
			System.out.println("empty");
		}
		return beans;
	}

	/**
	 * Method For get all schools
	 **/
	public static List<SchoolBean> prepareListOfSchool(List<SchoolModel> schools) {

		List<SchoolBean> beans = null;
		if (schools != null && !schools.isEmpty()) {
			beans = new ArrayList<SchoolBean>();
			SchoolBean bean = null;
			for (SchoolModel school : schools) {
				bean = new SchoolBean();
				bean.setEmail(school.getEmail());
				bean.setPrincipal_name(school.getPrincipal_name());
				bean.setS_id(school.getS_id());
				bean.setCity(school.getCity());
				bean.setCountry(school.getCountry());
				bean.setState(school.getState());
				bean.setContact_number(school.getContact_number());
				bean.setPrincipal_contact(school.getPrincipal_contact());
				bean.setZip_code(school.getZip_code());
				bean.setTotal_bus(school.getTotal_bus());
				bean.setTotal_students(school.getTotal_students());
				bean.setSchool_name(school.getSchool_name());
				bean.setSchool_logo(school.getSchool_logo());
				bean.setSchool_address(school.getSchool_address());
				bean.setSchool_address_field(school.getSchool_address_field());
				beans.add(bean);
			}
		} else {
			//System.out.println("empty Result");
		}
		return beans;

	}
	
	
	public static String saveImg(String filePath , String schoolname) {
        if(filePath == "" ) return "" ;
        
		int rand = RandomUtils.nextInt();
		String imageName = schoolname + rand + ".png"; 
		
		String[] parts = filePath.split("base64,");
		String part2 = parts[1]; // 034556
		
		String directory = Assets.SCHOOL_UPLOAD_PATH_SYS + imageName ;
		byte[] imageByteArray = Base64.decodeBase64(part2);

		try { 
		   FileOutputStream imageOutFile = new FileOutputStream(directory);
		   imageOutFile.write(imageByteArray);
		   imageOutFile.close();
		   
			
			
		}catch(Exception exo) {
			System.err.println(exo);
		}
		return imageName ;
		
	}
	
	/* Method for set school value */
	public static SchoolModel prepareModel(SchoolBean schoolbean) {
		SchoolModel school = new SchoolModel();
		// school.setCity(schoolbean.getCity());
		// school.setContact_number(schoolbean.getContact_number());
		// school.setCountry(schoolbean.getCountry());
		// school.setEmail(schoolbean.getEmail());
		// school.setPrincipal_contact(schoolbean.getPrincipal_contact());
		// school.setPrincipal_name(schoolbean.getPrincipal_name());
		school.setS_id(null);
		school.setSchool_name(schoolbean.getSchool_name());
		// school.setState(schoolbean.getState());
		school.setTotal_bus(schoolbean.getTotal_bus());
		school.setTotal_students(schoolbean.getTotal_students());
		// school.setZip_code(schoolbean.getZip_code());
		
		
		school.setSchool_address(schoolbean.getSchool_address());
		school.setSchool_lat(schoolbean.getSchool_lat());
		school.setSchool_lng(schoolbean.getSchool_lng());
		school.setCountry(schoolbean.getCountry());
		school.setCity(schoolbean.getCity());
		school.setSchool_address_field(schoolbean.getSchool_address_field());
		
		return school;
	}
	
	/* Method for get Student list */
	public static List<StudentBean> getStudentList(List<StudentModel> students ,SchoolService schoolservice ) {

		List<StudentBean> beans = null;
		if (students != null && !students.isEmpty()) {
			beans = new ArrayList<StudentBean>();
			StudentBean bean = null;
			for (StudentModel student : students) {
				bean = new StudentBean();
				SchoolModel stud = new SchoolModel();
				stud = schoolservice.getSchoolById(student.getS_school_id());
				bean.setStudent_id(student.getStudent_id());
				bean.setSchool_name(stud.getSchool_name());
				bean.setS_fname(student.getS_fname());
				
				RouteModel rm=schoolservice.getRouteById(student.getS_route_id());
				if(rm!=null)
				{
					bean.setS_lname(rm.getRoute_name());
				}else
				{
					bean.setS_lname("N/A");
				}
				bean.setS_email(student.getS_email());
				bean.setS_address(student.getS_address());
				bean.setS_contact(student.getS_contact());
				bean.setS_image_path(student.getS_image_path());
				bean.setFamily_name(student.getFamily_name());
				bean.setFather_name(student.getFather_name());
				bean.setDob(student.getDob());
				bean.setGender(student.getGender());
				bean.setGrand_name(student.getGrand_name());
				bean.setNationality(student.getNationality());
				bean.setStudent_class(student.getStudent_class());
				bean.setGender(student.getGender());
				bean.setBlink_status(student.getBlink_status());
				bean.setBlood_type(student.getBlood_type());
				if(student.getP_1()!=null && student.getP_1()!=0)
				{
					LoginModel p1=schoolservice.getParentById(student.getP_1());
					
					bean.setPp_1(p1.getFirst_name()+" "+p1.getLast_name());
					bean.setR_1(student.getR_1());
				}else
				{
					bean.setPp_1("N/A");
					bean.setR_1("N/A");
				}
				
				if(student.getP_2()!=null && student.getP_2()!=0)
				{
					LoginModel p2=schoolservice.getParentById(student.getP_2());
					if(p2!=null){
						bean.setPp_2(p2.getFirst_name()+" "+p2.getLast_name());
						bean.setR_2(student.getR_2());
					}else{
						bean.setPp_2("N/A");
						bean.setR_2("N/A");
					}
					
				}else
				{
					bean.setPp_2("N/A");
					bean.setR_2("N/A");
				}
				
				if(student.getP_3()!=null && student.getP_3()!=0)
				{
					LoginModel p3=schoolservice.getParentById(student.getP_3());
					
					bean.setPp_3(p3.getFirst_name()+" "+p3.getLast_name());
					bean.setR_3(student.getR_2());
				}else
				{
					bean.setPp_3("N/A");
					bean.setR_3("N/A");
				}
				beans.add(bean);
			}
		} else {
			System.out.println("empty");
		}
		return beans;
	}
	
	
	/* Method for get country list */
	public static List<LoginBean> getParentList(List<LoginModel> users  , SchoolService schoolservice ) {

		List<LoginBean> beans = null;
		if (users != null && !users.isEmpty()) {
			beans = new ArrayList<LoginBean>();
			LoginBean bean = null;
			for (LoginModel user : users) {

				bean = new LoginBean();
				SchoolModel stud = new SchoolModel();
				stud = schoolservice.getSchoolById(user.getSchool_id());
				bean.setUser_id(user.getUser_id());
				bean.setSchool_name(stud.getSchool_name());
				bean.setP_status(user.getP_status());
				bean.setFirst_name(user.getFirst_name());
				bean.setLast_name(user.getLast_name());
				bean.setUser_name(user.getUser_name());
				bean.setUser_email(user.getUser_email());
				bean.setContact_number(user.getContact_number());
				bean.setPermission(user.getPermission());
				bean.setUser_pass(user.getUser_pass());
				bean.setFamily_name(user.getFamily_name());
				bean.setMiddle_name(user.getMiddle_name());
				bean.setMobile_number(user.getMobile_number());
				beans.add(bean);
			}
		} else {
			System.out.println("empty");
		}
		return beans;
	}
	
	
	public static String  getMonth(String monthname) {
		
		String month = "" ;
		
		if(monthname.equals("January"))
		{
			month="01";
			
		}else if (monthname.equals("February")) {
			month="02";
			
		}else if (monthname.equals("March")) {
			month="03";
			
		}else if (monthname.equals("April")) {
			month="04";
		
		}else if (monthname.equals("May")) {
			month="05";
			
		}else if (monthname.equals("June")) {
			month="06";
		}else if (monthname.equals("July")) {
			month="07";
		
		}else if (monthname.equals("August")) {
			month="08";
			
		}else if (monthname.equals("September")) {
			month="09";
		
		}else if (monthname.equals("October")) {
			month="10";
			
		}else if (monthname.equals("November")) {
			month="11";
			
		}else {
			month="12";
		}
		
	   return month ;	
	}
	
//	Iterate all Subscribers
	public static List<SubscriberBean> iterateAllSubscribers(List<SubscriberModel> faq_model)
	{
		List<SubscriberBean> feature_bean=null;
		if(faq_model!=null &&  !faq_model.isEmpty())
		{
			feature_bean=new ArrayList<SubscriberBean>();
			for(SubscriberModel p_model : faq_model)
			{
				SubscriberBean s_bean=new SubscriberBean();
				s_bean.setSub_id(p_model.getSub_id());
				s_bean.setEmail(p_model.getEmail());
				feature_bean.add(s_bean);
			}
		}
		return feature_bean;
	}
	
	//Method for iterate sliders
			public static List<CategoryContactBean> iterateAllCategory(List<CategoryContactModel> categoryModel)
			{
				List<CategoryContactBean> category_bean=null;
				if(categoryModel!=null &&  !categoryModel.isEmpty())
				{
					category_bean=new ArrayList<CategoryContactBean>();
					for(CategoryContactModel category_contact : categoryModel)
					{
						CategoryContactBean s_bean=new CategoryContactBean();
						s_bean.setC_cat_id(category_contact.getC_cat_id());
						s_bean.setCategory_en(category_contact.getCategory_en());
						s_bean.setCategory_ar(category_contact.getCategory_ar());
						category_bean.add(s_bean);
					}
				}else
				{
					category_bean.add(null);
				}
				return category_bean;
				
				
			}

//			Iterate all pages
			public static List<FeaturesBean> iterateAllFeatures(List<FeaturesModel> feature_model)
			{
				List<FeaturesBean> feature_bean=null;
				if(feature_model!=null &&  !feature_model.isEmpty())
				{
					feature_bean=new ArrayList<FeaturesBean>();
					for(FeaturesModel p_model : feature_model)
					{
						FeaturesBean s_bean=new FeaturesBean();
						s_bean.setFeatures_id(p_model.getFeatures_id());
						s_bean.setTitle(p_model.getTitle());
						s_bean.setContent(p_model.getContent());
						s_bean.setImage_path(p_model.getImage_path());
						feature_bean.add(s_bean);
					}
				}
				return feature_bean;
			}
		
			/**
			 * Method for get all hodiday list
			 * **/
			 
			public static List<HolidayDeletedBean> getDeletedHolidayList(List<HolidayDeletedModel> holiday) {

				List<HolidayDeletedBean> beans = null;
				if (holiday != null && !holiday.isEmpty()) {
					beans = new ArrayList<HolidayDeletedBean>();
					HolidayDeletedBean bean = null;
					for (HolidayDeletedModel holi : holiday) {
						bean = new HolidayDeletedBean();
						bean.setDel_id(holi.getDel_id());
						bean.setHoliday_id(holi.getHoliday_id());
						bean.setSchool_id(holi.getSchool_id());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
		
			/* Method for get holiday list by current month */
			public static List<HolidayBean> getHolidays(List<HolidayModel> holidays) {

				List<HolidayBean> beans = null;
				if (holidays != null && !holidays.isEmpty()) {
					beans = new ArrayList<HolidayBean>();
					HolidayBean bean = null;
					for (HolidayModel holiday : holidays) {
						bean = new HolidayBean();
						bean.setHoliday_date(holiday.getHoliday_date());
						bean.setHoliday_name(holiday.getHoliday_name());
						bean.setH_id(holiday.getH_id());
						bean.setSchool_id(holiday.getSchool_id());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}

			
			/* Method for route latlng */
			public static List<DriverTrack> getDriverTrackList(
					List<DriverTrackModel> tracklist , SchoolService schoolservice) {
				List<DriverTrack> beans = null;
				if (tracklist != null && !tracklist.isEmpty()) {
					beans = new ArrayList<DriverTrack>();
					DriverTrack bean = null;
					for (DriverTrackModel t_list : tracklist) {
						bean = new DriverTrack();
						bean.setRoute_id(t_list.getRoute_id());
						bean.setLng(t_list.getLng());
						bean.setLat(t_list.getLat());
						bean.setTrack_date(t_list.getTrack_date());
						bean.setTrack_id(t_list.getTrack_id());
						bean.setSpeed(t_list.getSpeed());
						RouteModel r=schoolservice.getRouteById(t_list.getRoute_id());
						DriverModel driver_details=schoolservice.getDriverById(t_list.getDriver_id());
						bean.setRoute_name(r.getRoute_name());
						bean.setDriver_name(driver_details.getDriver_fname());
						beans.add(bean);
					}
				} else {
					System.out.println("No Data Available");
				}
				return beans;
			}

			/* Method for get latlng of route */
			public static List<RouteLatLng> getAllLatLng(List<RouteLatLngModel> latlng , StudentService studentservice) {

				List<RouteLatLng> beans = null;
				if (latlng != null && !latlng.isEmpty()) {
					beans = new ArrayList<RouteLatLng>();
					RouteLatLng bean = null;
					for (RouteLatLngModel st : latlng) {
						bean = new RouteLatLng();
						
						
						StudentModel studentModel = studentservice.getStudentById(st.getStudent_id());
						if(studentModel!=null)
						{
							
							bean.setS_image(studentModel.getS_image_path());
							bean.setS_class(studentModel.getStudent_class());
							bean.setStudent_name(studentModel.getS_fname()+" "+studentModel.getFather_name()+" "+studentModel.getGrand_name()+" "+studentModel.getFamily_name());
							bean.setAddress(studentModel.getS_address());
							bean.setId(st.getId());
							bean.setLat(st.getLat());
							bean.setLng(st.getLng());
							bean.setRoute_id(st.getRoute_id());
							bean.setBlink_s(studentModel.getBlink_status());
							bean.setStudent_id(studentModel.getStudent_id());
							beans.add(bean);
						}else
						{
							bean.setS_image("");
							bean.setS_class("");
							bean.setStudent_name("");
							bean.setStudent_id(0);
							bean.setAddress("");
							bean.setId(null);
							bean.setLat(null);
							bean.setLng(null);
							bean.setRoute_id(null);
							bean.setBlink_s(0);
							beans.add(bean);
						}
						
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}

			/* Method for get Student list */
			public static List<RouteBean> getAllRoute(List<RouteModel> route) {

				List<RouteBean> beans = null;
				if (route != null && !route.isEmpty()) {
					beans = new ArrayList<RouteBean>();
					RouteBean bean = null;
					for (RouteModel st : route) {
						bean = new RouteBean();
						bean.setRoute_id(st.getRoute_id());
						bean.setRoute_name(st.getRoute_name());
						bean.setSource(st.getSource());
						bean.setDestination(st.getDestination());
						bean.setNote(st.getNote());
						bean.setSource_note(st.getSource_note());
						bean.setDestination_note(st.getDestination_note());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			/**
			 * Method for get all hodiday list
			 * **/
			public static List<HolidayBean> getHolidayList(List<HolidayModel> holiday) {

				List<HolidayBean> beans = null;
				if (holiday != null && !holiday.isEmpty()) {
					beans = new ArrayList<HolidayBean>();
					HolidayBean bean = null;
					for (HolidayModel holi : holiday) {
						bean = new HolidayBean();
						bean.setH_id(holi.getH_id());
						bean.setHoliday_date(holi.getHoliday_date());
						bean.setHoliday_name(holi.getHoliday_name());
						bean.setHoliday_enddate(holi.getHoliday_enddate());
						bean.setSchool_id(holi.getSchool_id());
						bean.setHoliday_start_date(holi.getHoliday_start_date());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			/**
			 * Method for get all hodiday list
			 * **/
			public static List<AttendanceBean> getPresentList(List<AttendanceModel> presents) {

				List<AttendanceBean> beans = null;
				if (presents != null && !presents.isEmpty()) {
					beans = new ArrayList<AttendanceBean>();
					AttendanceBean bean = null;
					for (AttendanceModel present : presents) {
						bean = new AttendanceBean();
						bean.setA_id(present.getA_id());
						bean.setDate(present.getDate());
						bean.setLogin_date(present.getLogin_date());
						bean.setLogin_time(present.getLogin_time());
						bean.setLogout_time(present.getLogout_time());
						bean.setLogin_evening(present.getLogin_evening());
						bean.setLogout_evening(present.getLogout_evening());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			/* Method for get country list */
			public static List<LoginBean> getSuperList(List<LoginModel> users) {

				List<LoginBean> beans = null;
				if (users != null && !users.isEmpty()) {
					beans = new ArrayList<LoginBean>();
					LoginBean bean = null;
					for (LoginModel user : users) {
						bean = new LoginBean();
						bean.setUser_id(user.getUser_id());
						bean.setP_status(user.getP_status());
						bean.setFirst_name(user.getFirst_name());
						bean.setLast_name(user.getLast_name());
						bean.setUser_name(user.getUser_name());
						bean.setUser_email(user.getUser_email());
						bean.setContact_number(user.getContact_number());
						bean.setPermission(user.getPermission());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			/* Method for get country list */
			public static List<LoginBean> getSchoolAdminList(List<LoginModel> users,
					int user_id , SchoolService schoolservice) {

				List<LoginBean> beans = null;
				if (users != null && !users.isEmpty()) {
					beans = new ArrayList<LoginBean>();
					LoginBean bean = null;
					for (LoginModel user : users) {

						bean = new LoginBean();
						bean.setUser_id(user.getUser_id());
						int count = schoolservice.getAllMessageCount(user_id,
								user.getUser_id()).size();
						bean.setP_status(count);
						bean.setFirst_name(user.getFirst_name());
						bean.setLast_name(user.getLast_name());
						bean.setUser_name(user.getUser_name());
						bean.setUser_email(user.getUser_email());
						bean.setContact_number(user.getContact_number());
						bean.setPermission(user.getPermission());
						bean.setUser_pass(user.getUser_pass());
						bean.setSms_checked_in_on(user.getSms_checked_in_on());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			
			/* Method for get country list */
			public static List<SchoolClasses> getAllClassList(List<SchoolClassesModel> classes) {

				List<SchoolClasses> beans = null;
				if (classes != null && !classes.isEmpty()) {
					beans = new ArrayList<SchoolClasses>();
					SchoolClasses bean = null;
					for (SchoolClassesModel s_class : classes) {

						bean = new SchoolClasses();
						bean.setClass_id(s_class.getClass_id());
						bean.setClass_name(s_class.getClass_name());
						bean.setSchool_id(s_class.getSchool_id());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			/**
			 * Method for get all hodiday list
			 * **/
			public static  List<DriverAttendanceBean> getDriverPresentList(List<DriverAttendanceModel> presents) {

				List<DriverAttendanceBean> beans = null;
				if (presents != null && !presents.isEmpty()) {
					beans = new ArrayList<DriverAttendanceBean>();
					DriverAttendanceBean bean = null;
					for (DriverAttendanceModel present : presents) {
						bean = new DriverAttendanceBean();
						bean.setA_id(present.getA_id());
						bean.setDriver_id(present.getDriver_id());
						bean.setLogin_date(present.getLogin_date());
						bean.setLogin_time(present.getLogin_time());
						bean.setLogout_time(present.getLogout_time());
						bean.setStatus(present.getStatus());
						bean.setLogin_evening(present.getLogin_evening());
						bean.setLogout_evening(present.getLogout_evening());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			/* Method for get country list */
			public static List<CityBean> prepareListofCities(List<CityModel> cities) {

				List<CityBean> beans = null;
				if (cities != null && !cities.isEmpty()) {
					beans = new ArrayList<CityBean>();
					CityBean bean = null;
					for (CityModel city : cities) {
						bean = new CityBean();
						bean.setCity_id(city.getCity_id());
						bean.setCity_name(city.getCity_name());
						bean.setCountry_id(city.getCountry_id());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			/**
			 * Method For get all schools
			 **/
			public static List<VehicleDocBean> prepareListOfVehicleDoc(List<VehicleDocModel> vehicles) {

				List<VehicleDocBean> beans = null;
				if (vehicles != null && !vehicles.isEmpty()) {
					beans = new ArrayList<VehicleDocBean>();
					VehicleDocBean bean = null;
					for (VehicleDocModel vehicle : vehicles) {
						bean = new VehicleDocBean();
						bean.setInsurance_card_copy(vehicle.getInsurance_card_copy());
						bean.setInsurance_document_expiry(vehicle.getInsurance_document_expiry());
						bean.setInsurance_document_name(vehicle.getInsurance_document_name());
						bean.setRemind_day(vehicle.getRemind_day());
						bean.setSchool_id(vehicle.getSchool_id());
						bean.setV_doc_id(vehicle.getV_doc_id());
						bean.setV_id(vehicle.getV_id());
						bean.setStatus(vehicle.getStatus());
						bean.setInsurance_end_date(vehicle.getInsurance_end_date());
						beans.add(bean);
					}
				} else {
					//System.out.println("empty Result");
				}
				return beans;

			}
			
			/**
			 * Method For get all schools
			 **/
			public static List<DriverDocBean> prepareListOfDriverDoc(List<DriverDocModel> drivers) {

				List<DriverDocBean> beans = null;
				if (drivers != null && !drivers.isEmpty()) {
					beans = new ArrayList<DriverDocBean>();
					DriverDocBean bean = null;
					for (DriverDocModel vehicle : drivers) {
						bean = new DriverDocBean();
						bean.setInsurance_card_copy(vehicle.getInsurance_card_copy());
						bean.setInsurance_document_expiry(vehicle.getInsurance_document_expiry());
						bean.setInsurance_document_name(vehicle.getInsurance_document_name());
						bean.setRemind_day(vehicle.getRemind_day());
						bean.setSchool_id(vehicle.getSchool_id());
						bean.setV_doc_id(vehicle.getV_doc_id());
						bean.setV_id(vehicle.getV_id());
						bean.setStatus(vehicle.getStatus());
						bean.setInsurance_end_date(vehicle.getInsurance_end_date());
						beans.add(bean);
					}
				} else {
					//System.out.println("empty Result");
				}
				return beans;

			}
			
//			Method for iterate nationlity list
			public static List<NationlityBean> prepareNationalityList(List<NationlityModel> nationModel)
			{
				List<NationlityBean> beans = null;
				if (nationModel != null && !nationModel.isEmpty()) {
					beans = new ArrayList<NationlityBean>();
					NationlityBean bean = null;
					for (NationlityModel nation : nationModel) {
						bean = new NationlityBean();
						bean.setName(nation.getName());
						bean.setNational_id(nation.getNational_id());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
			public static List<LocalDate> getWeekendDates
			    (LocalDate start, LocalDate end,String dayName)
			{
			    List<LocalDate> result = new ArrayList<LocalDate>();
			    for (LocalDate date = start;
			         date.isBefore(end);
			         date = date.plusDays(1))
			    {
			        int day = (Integer)date.getDayOfWeek();
			        // These could be passed in...
			        if(dayName.equals("SATURDAY"))
			        {
			        	if (day == DateTimeConstants.SATURDAY)
				        {
				            result.add(date);
				        }
			        }
			        if(dayName.equals("SUNDAY"))
			        {
			        	if (day == DateTimeConstants.SUNDAY)
				        {
				            result.add(date);
				        }
			        }
			        if(dayName.equals("MONDAY"))
			        {
			        	if (day == DateTimeConstants.MONDAY)
				        {
				            result.add(date);
				        }
			        }
			        if(dayName.equals("TUESDAY"))
			        {
			        	if (day == DateTimeConstants.TUESDAY)
				        {
				            result.add(date);
				        }
			        }
			        if(dayName.equals("WEDNESDAY"))
			        {
			        	if (day == DateTimeConstants.WEDNESDAY)
				        {
				            result.add(date);
				        }
			        }
			        if(dayName.equals("THURSDAY"))
			        {
			        	if (day == DateTimeConstants.THURSDAY)
				        {
				            result.add(date);
				        }
			        }
			        if(dayName.equals("FRIDAY"))
			        {
			        	if (day == DateTimeConstants.FRIDAY)
				        {
				            result.add(date);
				        }
			        }
			        
			        
			    }
			    return result;
			} 
			
			/* Method for get relationship list */
			public static List<relationshipBean> getRelationshipList(List<relationshipModel> relationships) {

				List<relationshipBean> beans = null;
				if (relationships != null && !relationships.isEmpty()) {
					beans = new ArrayList<relationshipBean>();
					relationshipBean bean = null;
					for (relationshipModel rel : relationships) {
						bean = new relationshipBean();
						bean.setR_id(rel.getR_id());
						bean.setR_title(rel.getR_title());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}
			
//			 Method for iterate sliders
			public static List<SliderBean> iterateAllSlider(List<SliderModel> sliders)
			{
				List<SliderBean> slider_bean=null;
				if(sliders!=null &&  !sliders.isEmpty())
				{
					slider_bean=new ArrayList<SliderBean>();
					for(SliderModel slider : sliders)
					{
						SliderBean s_bean=new SliderBean();
						s_bean.setSlider_id(slider.getSlider_id());
						s_bean.setSlider_type(slider.getSlider_type());
						s_bean.setSlider_image(slider.getSlider_image());
						slider_bean.add(s_bean);
					}
				}else
				{
					slider_bean.add(null);
				}
				return slider_bean;
				
				
			}
			
			public static List<PageBean> iterateAllPage(List<PageModel> page_model)
			{
				List<PageBean> page_bean=null;
				if(page_model!=null &&  !page_model.isEmpty())
				{
					page_bean=new ArrayList<PageBean>();
					for(PageModel p_model : page_model)
					{
						PageBean s_bean=new PageBean();
						s_bean.setPage_id(p_model.getPage_id());
						s_bean.setPage_name(p_model.getPage_name());
						s_bean.setPage_title(p_model.getPage_title());
						s_bean.setPage_desc(p_model.getPage_desc());
						page_bean.add(s_bean);
					}
				}else
				{
					page_bean.add(null);
				}
				return page_bean;
			}
			
			/* Method for route latlng */
			public static List<VechileBean> getDriverVehicleList(
					List<VehicleModel> tracklist) {
				List<VechileBean> beans = null;
				if (tracklist != null && !tracklist.isEmpty()) {
					beans = new ArrayList<VechileBean>();
					VechileBean bean = null;
					for (VehicleModel t_list : tracklist) {
						bean = new VechileBean();
						bean.setBus_number(t_list.getBus_number());
						bean.setVechile_id(t_list.getVechile_id());
						bean.setVehile_name(t_list.getVehile_name());
						beans.add(bean);
					}
				} else {
					System.out.println("No Data Available");
				}
				return beans;
			}

			
//			Iterate all pages
			public static List<FaqBean> iterateAllFaq(List<FaqModel> faq_model)
			{
				List<FaqBean> feature_bean=null;
				if(faq_model!=null &&  !faq_model.isEmpty())
				{
					feature_bean=new ArrayList<FaqBean>();
					for(FaqModel p_model : faq_model)
					{
						FaqBean s_bean=new FaqBean();
						s_bean.setFaq_id(p_model.getFaq_id());
						s_bean.setCategory(p_model.getCategory());
						s_bean.setQuestion(p_model.getQuestion());
						s_bean.setAnswer(p_model.getAnswer());
						feature_bean.add(s_bean);
					}
				}
				return feature_bean;
			}

			/**
			 * Method for get location from lat lng
			 **/
			public static GoogleResponse convertFromLatLong(String latlongString)
					throws IOException {

				/*
				 * Create an java.net.URL object by passing the request URL in
				 * constructor. Here you can see I am converting the fullAddress String
				 * in UTF-8 format. You will get Exception if you don't convert your
				 * address in UTF-8 format. Perhaps google loves UTF-8 format. :) In
				 * parameter we also need to pass "sensor" parameter. sensor (required
				 * parameter) — Indicates whether or not the geocoding request comes
				 * from a device with a location sensor. This value must be either true
				 * or false.
				 */
				URL url = new URL(Assets.URL + "?latlng="
						+ URLEncoder.encode(latlongString, "UTF-8") + "&sensor=false");
				// Open the Connection
				URLConnection conn = url.openConnection();

				InputStream in = conn.getInputStream();
				ObjectMapper mapper = new ObjectMapper();
				GoogleResponse response = (GoogleResponse) mapper.readValue(in,
						GoogleResponse.class);
				in.close();
				return response;

			}

			/**
			 * Method for get latlng from address using google api
			 **/
			public static GoogleResponse convertToLatLong(String fullAddress)
					throws IOException {

				/*
				 * Create an java.net.URL object by passing the request URL in
				 * constructor. Here you can see I am converting the fullAddress String
				 * in UTF-8 format. You will get Exception if you don't convert your
				 * address in UTF-8 format. Perhaps google loves UTF-8 format. :) In
				 * parameter we also need to pass "sensor" parameter. sensor (required
				 * parameter) — Indicates whether or not the geocoding request comes
				 * from a device with a location sensor. This value must be either true
				 * or false.
				 */

				URL url = new URL(Assets.URL + "?address="
						+ URLEncoder.encode(fullAddress, "UTF-8") + "&sensor=false");
				// Open the Connection
				URLConnection conn = url.openConnection();

				InputStream in = conn.getInputStream();
				ObjectMapper mapper = new ObjectMapper();
				GoogleResponse response = (GoogleResponse) mapper.readValue(in,
						GoogleResponse.class);
				in.close();
				return response;

			}
			
			/* Method for get country list */
			public static List<VechileBean> getAllVechiles(List<VehicleModel> vechiles ,SchoolService schoolservice ) {

				List<VechileBean> beans = null;
				if (vechiles != null && !vechiles.isEmpty()) {
					beans = new ArrayList<VechileBean>();
					VechileBean bean = null;
					for (VehicleModel vechile : vechiles) {
						bean = new VechileBean();
						SchoolModel stud = new SchoolModel();
						stud = schoolservice.getSchoolById(vechile.getSchool_id());
						bean.setBus_number(vechile.getBus_number());
						if(vechile.getDriver_id()!=null && vechile.getDriver_id()!=0)
						{
							DriverModel d=schoolservice.getDriverById(vechile.getDriver_id());
							if(d!=null)
							{
								bean.setColor(d.getDriver_fname()+" "+d.getDriver_lname());
							}else
							{
								bean.setColor("N/A");
							}
							
						}else
						{
							bean.setColor("N/A");
						}
						bean.setConfigurtion(vechile.getConfigurtion());
						bean.setEngine(vechile.getEngine());
						bean.setManufacture(vechile.getManufacture());
						bean.setModel(vechile.getModel());
						bean.setVechile_id(vechile.getVechile_id());
						bean.setVehile_name(vechile.getVehile_name());
						bean.setYear(vechile.getYear());
						bean.setSchool_name(stud.getSchool_name());
						bean.setInsurance_end_date(vechile.getInsurance_end_date());
						beans.add(bean);
					}
				} else {
					System.out.println("empty");
				}
				return beans;
			}

}


/* CustomerController.java
 *
 * Copyright (C) 2013 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 * 
 */

package controllers;


import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import services.CensusService;
import services.UserService;

import domain.Census;
import domain.User;

@Controller
@RequestMapping("/census")
public class CensusController extends AbstractController {

	
	@Autowired
	private CensusService censusService;
	
	@Autowired
	private UserService userService;
	// Constructors -----------------------------------------------------------

	public CensusController() {
		super();
	}

	// Create census ---------------------------------------------------------------		

	@RequestMapping("/create")
	public ModelAndView create() {
		ModelAndView result;
		Census  c = censusService.create();
		result = createEditModelAndView(c);

		return result;
	}
	
	// Devuelve un censo en formato JSON ---------------------------------------------------------------	
	
	 @RequestMapping(value = "/json_one", method = RequestMethod.GET, produces="application/json")  
	 public @ResponseBody  Census getCensus(@RequestParam int censusId) {  
		 Census c = new Census();
		 c = censusService.findOne(censusId);
		 return c;
	 }  
	 
	// Devuelve todos los censos en formato JSON ---------------------------------------------------------------	
	 
	 @RequestMapping(value ="/json_all", method = RequestMethod.GET, produces="application/json")    
	 public @ResponseBody  Collection<Census> getAllCensus() {  
		 Collection<Census> cs;
		 cs = censusService.findAll();
		 return cs;
	 }  
	 
	 
	 
	 
	// Add Users ----------------------------------------------------------------
	@RequestMapping(value = "/addUser", method = RequestMethod.GET)
	public ModelAndView addUser(@RequestParam int censusId, int userId) {
		ModelAndView result = null;
		try{
			User u = userService.findOne(userId);
			Census c = censusService.findOne(censusId);
			Collection<User> users = c.getUsers();//Usuarios del censo a editar
			Collection<Census> censues = u.getCensuses();//Censos del usuario a editar
			
			users.add(u);//Usuarios del censo a editar
			censues.add(c);
			
			censusService.save(c);
			userService.save(u);
			
			result = new ModelAndView("redirect:/census/edit.do?censusId="+censusId);
			
		}catch(Exception oops){
			result = new ModelAndView("redirect:/census/edit.do?censusId="+censusId);
			result.addObject("message", "No se pudo añadir el usuario");
			oops.getStackTrace();
		}
		

		return result;
	}
	
	// Remove Users ----------------------------------------------------------------
	@RequestMapping(value = "/removeUser", method = RequestMethod.GET)
	public ModelAndView removeUser(@RequestParam int censusId, int userId) {
		ModelAndView result = null;
		try{
			User u = userService.findOne(userId);
			Census c = censusService.findOne(censusId);
			Collection<User> users = c.getUsers();//Usuarios del censo a editar
			Collection<Census> censues = u.getCensuses();//Censos del usuario a editar
			
			users.remove(u);//Usuarios del censo a editar
			censues.remove(c);
			
			censusService.save(c);
			userService.save(u);
			
			result = new ModelAndView("redirect:/census/edit.do?censusId="+censusId);
			
		}catch(Exception oops){
			result = new ModelAndView("redirect:/census/edit.do?censusId="+censusId);
			result.addObject("message", "No se pudo eliminar el usuario");
			oops.getStackTrace();
		}
		

		return result;
	}

	// Lista los censos del sistema  ---------------------------------------------------------------	

	@RequestMapping("/list")
	public ModelAndView list(){
		String requestURI = "census/list.do";
		ModelAndView result = new ModelAndView("census/list");
		result.addObject("censuses", censusService.findAll());
		result.addObject("requestURI", requestURI);
		return result;
	}
	
	
	// Details ----------------------------------------------------------------
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ModelAndView details(@RequestParam int censusId) {
		ModelAndView result;
		Census census= censusService.findOne(censusId);
		result=createEditModelAndView(census);

		return result;
	}
	
	// Editar un censo para añadir o quitar usuarios ---------------------------------------------------------------	

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam int censusId) {
		ModelAndView result = new ModelAndView("census/manage");
		Census census= censusService.findOne(censusId);
		Collection <User> users = userService.findAll();
		result.addObject("users", users);
		result.addObject("census", census);
		

		return result;
	}
	
	// Delete ----------------------------------------------------------------
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam int censusId) {
		ModelAndView result = null;
		try{
			censusService.delete(censusId);
			result = new ModelAndView("redirect:/census/list.do");
			
		}catch(Exception oops){
			result = new ModelAndView("redirect:/census/details.do?censusId="+censusId);
			result.addObject("message", "No se pudo borrar el censo");
			oops.getStackTrace();
		}
		

		return result;
	}
		

	//Save
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid Census census, BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors()) {
			result = createEditModelAndView(census);
		} else {
			try {
				censusService.save(census);
				result = new ModelAndView("redirect:/census/list.do");
				
			}catch(DataIntegrityViolationException uups){
				result = createEditModelAndView(census, "census.duplicated.name");	 
			
			} catch (Throwable oops) {
				result = createEditModelAndView(census, "census.commit.error");				
			}
		}
		
		return result;
	}
	
	// Ancillary methods ------------------------------------------------------
	
	protected ModelAndView createEditModelAndView(Census census) {
		ModelAndView result;

		result = createEditModelAndView(census, null);
		
		return result;
	}	
	
	protected ModelAndView createEditModelAndView(Census census, String message) {
		ModelAndView result = new ModelAndView("census/create");
		
		if(census.getId() != 0){
			result = new ModelAndView("census/details");
			//result.addObject("users", userService.findAllByCensus(census.getId()));
			result.addObject("users", census.getUsers());
		}
		
		
		
		result.addObject("census", census);
		result.addObject("message", message); //esta definida en el layout, no en la vista edit
		
		return result;

	}
	
}	
	
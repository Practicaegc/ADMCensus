package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import repositories.UserRepository;
import domain.User;


@Service
@Transactional
public class UserService {
	
	// Managed repository -----------------------------------------------------
	@Autowired
	private UserRepository userRepository;
	
	// Supporting services ----------------------------------------------------
	
	
	
	// Constructors -----------------------------------------------------------
	public UserService(){
		super();
	}
	
	// CRUDs Methods -----------------------------------------------------------
	
	public Collection<User> findAll(){
		return userRepository.findAll();
	}
	
	public User findOne(int censusId){
		
		return userRepository.findOne(censusId);
		
	}
	
	
	public User save(User user){
		//Assert.isTrue(actorService.isAdministrator());
		User u = userRepository.save(user);
		return u;
	}
	

	

}
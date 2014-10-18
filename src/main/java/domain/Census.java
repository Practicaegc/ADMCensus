package domain;

import java.util.Collection;
import java.util.Date;
import javax.xml.bind.annotation.*;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;


@Entity
@XmlRootElement(name="census")
@Access(AccessType.PROPERTY)
public class Census extends DomainEntity{
	
	private Date date;
	private String name;
	
	private Collection<User> users;
	
	
	public Census(){
		
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@NotNull
	@Past
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@NotBlank
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@NotNull
	@ManyToMany
	@XmlElementWrapper(name="users")
	@XmlElement(name="name")
	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}
	
	
	
	
	

}

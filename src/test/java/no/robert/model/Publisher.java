package no.robert.model;

import static org.hibernate.annotations.CascadeType.PERSIST;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Publisher {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    private Long id;

    private String name;
    
    @OneToOne
    @Cascade({SAVE_UPDATE, PERSIST})
    private Editor editor;

    public Publisher()
    {
        
    }

    public Publisher(String name)
    {
        this.setName(name);
    }

    public Publisher( String name, Editor editor )
    {
        this.name = name;
        this.editor = editor;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    public void setEditor( Editor editor )
    {
        this.editor = editor;
    }
    
    public Editor getEditor()
    {
        return editor;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}

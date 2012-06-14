package no.robert.repository.fluenty;

import static javax.persistence.Persistence.createEntityManagerFactory;
import static no.robert.methodref.MethodRef.on;
import static no.robert.repository.fluenty.Where.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;

import no.robert.model.*;
import no.robert.repository.Repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RepositoryTest {

	private Repository repository;
	private EntityManager entityManager;
	private Book someBook, anotherBook, ourBook;
	private Author author, anotherAuthor;
	private Publisher publisher, anotherPublisher;

	@Before
	public void setupPersistenceContext() {
		entityManager = createEntityManagerFactory("no.robert.fluenty").createEntityManager();
		repository = new Repository();
		repository.setEntityManager(entityManager);

		entityManager.getTransaction().begin();

		author = new Author("Rune Flobakk");
		anotherAuthor = new Author("Robert Larsen");

		publisher = new Publisher("Manning");
		anotherPublisher = new Publisher("Addison-Wesley");
		
		anotherBook = new Book("Title", anotherAuthor, 10, anotherPublisher);
		someBook = new Book("Some booktitle", author, 1337, publisher);
		ourBook = new Book("Some other booktitle", author, 20, publisher);
		entityManager.persist(ourBook);
		entityManager.persist(someBook);
		entityManager.persist(anotherBook);
		

	}
	
	@Test
	public void getAll() {
		List<Book> books = repository.find(Book.class, having(on(Book.class)).getAll());
		assertThat(books, hasSize(3));
		assertThat( books, hasItems(ourBook, someBook, anotherBook));
	}

	@Test
	public void specifySinglePropertyEqual() {
		List<Book> books = repository.find(Book.class, having(on(Book.class).getTitle()).equal("Some non-existing title"));
		assertTrue(books.isEmpty());
		
		Book book = repository.findSingle(Book.class, having(on(Book.class).getTitle()).equal("Some booktitle"));
		assertThat(book, is( someBook ));
	}
	
	@Test
	public void specifySinglePropertyGreaterThan() {

		List<Book> books = repository.find(Book.class, having(on(Book.class).getPages()).greaterThan(1338));
		assertTrue(books.isEmpty());

		books = repository.find(Book.class, having(on(Book.class).getPages()).greaterThan(9));
		assertThat(books, hasSize(3));
		assertThat(books, hasItems(ourBook, someBook, anotherBook));
		
		books = repository.find(Book.class, having(on(Book.class).getPages()).greaterThanOrEqualTo(20));
		assertThat(books, hasSize(2));
		assertThat(books, hasItems(someBook, ourBook));
		
	}

	@Test
	public void specifySinglePropertyLessThan() {
		
		List<Book> books = repository.find(Book.class, having(on(Book.class).getPages()).lessThan(11));
		assertThat(books, hasSize(1));
		assertThat(books, hasItem(anotherBook));

		books = repository.find(Book.class, having(on(Book.class).getPages()).lessThanOrEqualTo(1337));
		assertThat(books, hasSize(3));
		assertThat(books, hasItems(ourBook, someBook, anotherBook));
		
	}

	@Test
	public void specifySinglePropertyInChain() {
		List<Book> books = repository.find( Book.class, having( on( Book.class ).getPublisher().getName()).equal( "Manning" ) );
		assertThat(books, hasSize(2));
		assertThat(books, hasItems(someBook, ourBook));
	}

	@Test
	public void specifyCollectionProperty() {
		List<Book> books = repository.find(Book.class, having(on(Book.class).getAuthors()).with( author ) );
		assertThat( books, hasSize(2)); 
		assertThat( books, hasItems(someBook, ourBook));
	}

	@Test
	public void specifyCollectionPropertyInChain() {
		List<Book> books = repository.find( Book.class, 
				having(on(Book.class).getAuthors()).having(on(Author.class).getName()).equal( "Rune Flobakk" ));

		assertThat(books, hasSize(2));
		assertThat(books, hasItems(someBook, ourBook));
	}

	@After
	public void rollback() {
		entityManager.getTransaction().rollback();
	}


}
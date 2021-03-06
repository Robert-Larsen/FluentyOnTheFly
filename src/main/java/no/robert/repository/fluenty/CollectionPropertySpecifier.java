package no.robert.repository.fluenty;

import static no.robert.repository.fluenty.Strategies.asProperty;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import no.robert.methodref.MethodRef;

public class CollectionPropertySpecifier<PROP> implements CriteriaPopulator {

	private final MethodRef methodRef;
	private final CriteriaPopulator previous;
	private Path<?> previousPath;

	private PROP elementInCollection;

	public CollectionPropertySpecifier(MethodRef methodRef, CriteriaPopulator previous) {
		this.methodRef = methodRef;
		this.previous = previous;
		this.previousPath = null;
	}

	public CollectionPropertySpecifier<PROP> with(PROP element) {
		this.elementInCollection = element;
		return this;
	}
	
	public Path<?> getPreviousPath() {
		return this.previousPath;
	}

	public <NESTED_PROP> SinglePropertySpecifier<NESTED_PROP> having(NESTED_PROP name) {
		MethodRef nextMethodRef = MethodRef.get();
		return new SinglePropertySpecifier<NESTED_PROP>(nextMethodRef, this);
	}
	
	@Override
	public <T> void populate(CriteriaQuery<T> criteriaQuery, CriteriaBuilder builder) {
		
		previous.populate(criteriaQuery, builder);
		@SuppressWarnings("unchecked")
		Root<T> root = (Root<T>) criteriaQuery.from(methodRef.getTargetType());
		if( elementInCollection != null ) {
			Path<Collection<PROP>> path = root.get(asProperty(methodRef).getName());
			criteriaQuery.select(root).where(builder.isMember(elementInCollection, path));
		}
		else {
			this.previousPath = root.get(asProperty(methodRef).getName());
			criteriaQuery = criteriaQuery.select(root);
		}
	}
}
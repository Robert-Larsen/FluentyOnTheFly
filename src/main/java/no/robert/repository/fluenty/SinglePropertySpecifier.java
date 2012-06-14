package no.robert.repository.fluenty;

import static no.robert.repository.fluenty.Strategies.asProperty;

import java.util.Collection;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import no.robert.methodref.MethodRef;

public class SinglePropertySpecifier<PROP> implements CriteriaPopulator {


	private final MethodRef methodRef;
	private final CriteriaPopulator previous;
	private PROP propertyValue;
	private String constraintMethod;

	public SinglePropertySpecifier(MethodRef methodRef, CriteriaPopulator previous) {
		this.methodRef = methodRef;
		this.previous = previous;
	}

	private Expression<Boolean> invokeConstraintMethod(CriteriaBuilder builder, Path<?> property, PROP propertyValue) {
		if( constraintMethod.equals("eq"))
			return builder.equal(property, propertyValue);
		else {
			Number propValue = (Number) propertyValue;
			@SuppressWarnings("unchecked")
			Expression< ? extends Number> prop = (Expression<? extends Number>) property;
			if( constraintMethod.equals("gt"))			
				return builder.gt(prop, propValue);
			else if(constraintMethod.equals("ge"))
				return builder.ge(prop,  propValue);
			else if(constraintMethod.equals("lt"))
				return builder.lt(prop, propValue);
			else if(constraintMethod.equals("le"))
				return builder.le(prop, propValue);
		}
		return null;
	}

	public Path<?> getPreviousPath() {
		return null;
	}

	public SinglePropertySpecifier<PROP> getAll() {
		constraintMethod = "all";
		return this;
	}

	public SinglePropertySpecifier<PROP> equal(PROP value) {
		this.propertyValue = value;
		constraintMethod = "eq";
		return this;
	}

	public SinglePropertySpecifier<PROP> greaterThan( PROP value ) {
		this.propertyValue = value;
		constraintMethod = "gt";
		return this;
	}

	public SinglePropertySpecifier<PROP> greaterThanOrEqualTo( PROP value ) {
		this.propertyValue = value;
		constraintMethod = "ge";
		return this;
	}

	public SinglePropertySpecifier<PROP> lessThan( PROP value ) {
		this.propertyValue = value;
		constraintMethod = "lt";
		return this;
	}

	public SinglePropertySpecifier<PROP> lessThanOrEqualTo( PROP value ) {
		this.propertyValue = value;
		constraintMethod = "le";
		return this;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> void populate(CriteriaQuery<T> criteria, CriteriaBuilder builder) {

		if( constraintMethod.equals("all")) {
			Root root = criteria.from(criteria.getResultType());
			criteria.select(root);
		}
		else {
			previous.populate(criteria, builder);
			Set<Root<?>> roots = criteria.getRoots();
			Root root;
			if(roots.size() > 0) {
				root = roots.iterator().next();
				Expression<Collection<T>> previousPath = (Expression<Collection<T>>) previous.getPreviousPath();			
				Subquery<T> subquery = (Subquery<T>) criteria.subquery(methodRef.getTargetType());
				Root subroot = subquery.from(methodRef.getTargetType());
				subquery.select(subroot).where(invokeConstraintMethod(
						builder, subroot.get(asProperty(methodRef).getName()), propertyValue));

				
				criteria.where(builder.isMember(subquery, previousPath));
			}
			else {
				root = criteria.from(methodRef.getTargetType());
				Path<Object> path = root.get(asProperty(methodRef).getName());
				criteria.select(root);

				MethodRef next = methodRef.nextInChain();
				if( next.getReturnType() != null && !next.getReturnType().equals(Void.TYPE)) {
					while(next.getReturnType() != null && !next.getReturnType().equals(Void.TYPE)) {
						Subquery<?> subquery = criteria.subquery(next.getTargetType());
						Root subroot = subquery.from( next.getTargetType() );
						subquery.select(subroot).where(invokeConstraintMethod(
								builder, subroot.get(asProperty(next).getName()), propertyValue));
						criteria.where(builder.in(path).value(subquery));
						next = next.nextInChain();
					}
				}
				else {
					criteria.where(invokeConstraintMethod(
							builder, root.get(asProperty(methodRef).getName()), propertyValue));
				}
			}
		}
	}

}


/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.mapping.readwrite;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.metamodel.mapping.AttributeMapping;
import org.hibernate.metamodel.mapping.EntityMappingType;
import org.hibernate.metamodel.mapping.internal.BasicValuedSingularAttributeMapping;

import org.hibernate.testing.orm.junit.DomainModelScope;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SessionFactory
public abstract class AbstractReadWriteTests {
	@Test
	public void verifyModel(DomainModelScope scope) {
		scope.withHierarchy(
				ReadWriteEntity.class,
				(rootClass) -> {
					final Property property = rootClass.getProperty( "value" );
					final BasicValue valueMapping = (BasicValue) property.getValue();
					final Column column = (Column) valueMapping.getColumn();
					final String customRead = column.getCustomRead();
					assertThat( customRead, is( "conv * 1" ) );
				}
		);
	}

	@Test
	public void test(SessionFactoryScope scope) {
		final EntityMappingType entityMapping = scope.getSessionFactory()
				.getRuntimeMetamodels()
				.getEntityMappingType( ReadWriteEntity.class );
		final BasicValuedSingularAttributeMapping attribute = (BasicValuedSingularAttributeMapping) entityMapping.findAttributeMapping( "value" );
		attribute.forEachSelection(
				(i, selectable) -> {
					final String readExpression = selectable.getCustomReadExpression();
				}
		);

		scope.inTransaction(
				(session) -> {
					session.createQuery( "from ReadWriteEntity" ).list();
				}
		);
	}

	@Test
	public void testDisambiguity(SessionFactoryScope scope) {
		// more-or-less, make sure the read-fragment has its aliases handled.
		//
		// the double reference to the entity will mean we would have an
		// ambiguous reference to the underlying `conv` column

		scope.inTransaction(
				(session) -> {
					session.createQuery( "from ReadWriteEntity a, ReadWriteEntity b" ).list();
				}
		);
	}
}

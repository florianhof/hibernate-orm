/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.type.descriptor.java;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.descriptor.WrapperOptions;

/**
 * Descriptor for {@link BigInteger} handling.
 *
 * @author Steve Ebersole
 */
public class BigIntegerTypeDescriptor extends AbstractClassTypeDescriptor<BigInteger> {
	public static final BigIntegerTypeDescriptor INSTANCE = new BigIntegerTypeDescriptor();

	public BigIntegerTypeDescriptor() {
		super( BigInteger.class );
	}

	@Override
	public String toString(BigInteger value) {
		return value.toString();
	}

	@Override
	public BigInteger fromString(CharSequence string) {
		return new BigInteger( string.toString() );
	}

	@Override
	public int extractHashCode(BigInteger value) {
		return value.intValue();
	}

	@Override
	public boolean areEqual(BigInteger one, BigInteger another) {
		return one == another || ( one != null && another != null && one.compareTo( another ) == 0 );
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public <X> X unwrap(BigInteger value, Class<X> type, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( BigInteger.class.isAssignableFrom( type ) ) {
			return (X) value;
		}
		if ( BigDecimal.class.isAssignableFrom( type ) ) {
			return (X) new BigDecimal( value );
		}
		if ( Byte.class.isAssignableFrom( type ) ) {
			return (X) Byte.valueOf( value.byteValue() );
		}
		if ( Short.class.isAssignableFrom( type ) ) {
			return (X) Short.valueOf( value.shortValue() );
		}
		if ( Integer.class.isAssignableFrom( type ) ) {
			return (X) Integer.valueOf( value.intValue() );
		}
		if ( Long.class.isAssignableFrom( type ) ) {
			return (X) Long.valueOf( value.longValue() );
		}
		if ( Double.class.isAssignableFrom( type ) ) {
			return (X) Double.valueOf( value.doubleValue() );
		}
		if ( Float.class.isAssignableFrom( type ) ) {
			return (X) Float.valueOf( value.floatValue() );
		}
		throw unknownUnwrap( type );
	}

	@Override
	public <X> BigInteger wrap(X value, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( value instanceof BigInteger ) {
			return (BigInteger) value;
		}
		if ( value instanceof BigDecimal ) {
			return ( (BigDecimal) value ).toBigIntegerExact();
		}
		if ( value instanceof Number ) {
			return BigInteger.valueOf( ( (Number) value ).longValue() );
		}
		throw unknownWrap( value.getClass() );
	}

	@Override
	public long getDefaultSqlLength(Dialect dialect) {
		return getDefaultSqlPrecision(dialect)+1;
	}

	@Override
	public int getDefaultSqlPrecision(Dialect dialect) {
		return dialect.getDefaultDecimalPrecision();
	}

	@Override
	public int getDefaultSqlScale() {
		return 0;
	}

	@Override
	public <X> BigInteger coerce(X value, CoercionContext coercionContext) {
		if ( value == null ) {
			return null;
		}

		if ( value instanceof BigInteger ) {
			return (BigInteger) value;
		}

		if ( value instanceof Byte ) {
			return BigInteger.valueOf( ( (Byte) value ) );
		}

		if ( value instanceof Short ) {
			return BigInteger.valueOf( ( (Short) value ) );
		}

		if ( value instanceof Integer ) {
			return BigInteger.valueOf( ( (Integer) value ) );
		}

		if ( value instanceof Long ) {
			return BigInteger.valueOf( ( (Long) value ) );
		}

		if ( value instanceof Double ) {
			return CoercionHelper.toBigInteger( (Double) value );
		}

		if ( value instanceof Float ) {
			return CoercionHelper.toBigInteger( (Float) value );
		}

		if ( value instanceof BigDecimal ) {
			return CoercionHelper.toBigInteger( (BigDecimal) value );
		}

		if ( value instanceof String ) {
			return CoercionHelper.coerceWrappingError(
					() -> BigInteger.valueOf( Long.parseLong( (String) value ) )
			);
		}

		throw new CoercionException(
				String.format(
						Locale.ROOT,
						"Unable to coerce value [%s (%s)] to BigInteger",
						value,
						value.getClass().getName()
				)
		);
	}
}

package sample;

import com.javadude.annotation.Bean;
import com.javadude.annotation.Delegate;
import com.javadude.annotation.Property;
import com.javadude.annotation.PropertyKind;

@Bean(
        createPropertyMap = true,
        defineSimpleEqualsAndHashCode = true,
 	properties = {
		@Property(name = "basePerson", type = IPerson.class, bound = true),
        @Property(name = "title", type = String.class, kind = PropertyKind.INDEXED, bound = true)
	},
	delegates = {
		@Delegate(type = IPerson.class, property = "basePerson")
	}
)
public class Doctor extends DoctorGen implements IPerson {
	public Doctor(IPerson person) {
		setBasePerson(person);
        setTitle(new String[]{"Dr."});
	}
	@Override public String getName() {
		return getTitle(0) + super.getName();
	}
}

package sample;

import sample.Processor.Size;

import com.javadude.annotation.Bean;
import com.javadude.annotation.Default;

@Bean
public class DefaultParameterExample extends DefaultParameterExampleGen {
	protected void process(Processor processor, String item, @Default("Processor.Size.LARGE") Size size,
			               @Default("red") String color, @Default("1") int quantity) {
		processor.process(item, size, color, quantity);
	}

	public void report(@Default("Hello") String message) {
		System.out.println("Message: " + message);
	}
	public static void main(String[] args) {
		DefaultParameterExample e = new DefaultParameterExample();
		e.report();
		e.report("Goodbye");
		Processor processor = new DefaultProcessor();
		e.process(processor, "shirt", Size.SMALL, "blue", 2);
		e.process(processor, "shirt", Size.SMALL, "blue");
		e.process(processor, "shirt", Size.SMALL);
		e.process(processor, "shirt");
	}
}

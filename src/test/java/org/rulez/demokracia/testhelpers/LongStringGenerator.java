package org.rulez.demokracia.testhelpers;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LongStringGenerator {

	public static String generate(final int length) {
		return IntStream.range(0, length)
				.boxed()
				.map(a -> "w")
				.collect(Collectors.joining());
	}

}
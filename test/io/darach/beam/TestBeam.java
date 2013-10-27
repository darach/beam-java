// Copyright (c) 2013 Darach Ennis < darach at gmail dot com >.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:  
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.

package io.darach.beam;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class TestBeam {
	final PredicateFunction<Long> evenFn = new PredicateFunction<Long>() {
        public Boolean invoke(Long args) {
            return args % 2 == 0;
        }			
	};

	final PredicateFunction<Long> oddFn = new PredicateFunction<Long>() {
        public Boolean invoke(Long args) {
            return args % 2 == 1;
        }			
	};

	@Test
	public void eventEmitter() {
		final EventEmitter ee = new EventEmitter();
		final AtomicInteger count = new AtomicInteger();
		final Action onBeep = new Action() {
            public void onData(Object args) {
            	count.getAndIncrement();
            }
		};
		ee.on("beep", onBeep);
		ee.emit("boop", "blargh");
		ee.emit("boop", "blargh");
		ee.emit("beep", "boop");
		ee.emit("beep", "boop");
		
		assertEquals(2, count.get());
		
		ee.removeListener("beep",onBeep);
		
		ee.removeListener("boop",null);		
	}
	
	@Test
	public void badUsage() {
		final Source in = Beam.source();
		final Sink out = Beam.sink();
		try {
			in.pipe();
			fail("Should have blown chunks");
		} catch(Error ok) { }
		
		try {
			in.param(0);
		} catch(Exception ok) { }
		
		try {
			out.param(0);
		} catch(Exception ok) { }
	}
	@Test
	public void simplePipeline() {
		final Source in = Beam.source();
		assertSame(in, in.make());
		final Sink out = Beam.sink();
		assertSame(out, out.make());
		final Filter even = Beam.filter(evenFn);

		in.pipe(even).pipe(out);
	
		final List<Long> numbers = new LinkedList<Long>();
		
		out.on("data", new Action<Long>() {
            public void onData(Long data) {
            	numbers.add(data);
            }
		});
		
		for (long i = 1; i <= 10; i++) in.push(i);
		assertArrayEquals(new Long[] {2L,4L,6L,8L,10L}, numbers.toArray(new Long[]{}));
	}
	
	@Test
	public void simpleBranch() {
		final Source in = Beam.source();
		final Sink outEven = Beam.sink();
		final Sink outOdd = Beam.sink();
		final Filter even = Beam.filter(evenFn);
		final Filter odd = Beam.filter(oddFn);

		in.pipe(even).pipe(outEven);
		in.pipe(odd).pipe(outOdd);
	
		final List<Long> evens = new LinkedList<Long>();
		final List<Long> odds = new LinkedList<Long>();
		
		outEven.on("data", new Action<Long>() {
            public void onData(Long data) {
            	evens.add(data);
            }
		});
		
		outOdd.on("data", new Action<Long>() {
            public void onData(Long data) {
            	odds.add(data);
            }
		});

		for (long i = 1; i <= 10; i++) in.push(i);
		assertArrayEquals(new Long[] {2L,4L,6L,8L,10L}, evens.toArray(new Long[]{}));
		assertArrayEquals(new Long[] {1L,3L,5L,7L,9L}, odds.toArray(new Long[]{}));
	}
	
	@Test
	public void simpleCombine() {
		final Source in1 = Beam.source();
		final Source in2 = Beam.source();
		final Sink out = Beam.sink();
		
		in1.pipe(out);
		in2.pipe(out);

		final List<String> msgs = new LinkedList<String>();
		
		out.on("data", new Action<String>() {
            public void onData(String data) {
            	msgs.add(data);
            }
		});

		in1.push("beep");
		in2.push("boop");
		
		assertArrayEquals(new String[] {"beep","boop"}, msgs.toArray(new String[]{}));
	}
	
	public <T> void assertBif(final Operator op, final T a, final T r, int calls) {
		Source in = Beam.source();
		Beam sut = op.make();
		Sink out = Beam.sink();
		in.pipe(sut).pipe(out);
		final AtomicLong count = new AtomicLong();
		out.on("data", new Action<T>() {
			public void onData(T data) {
				assertEquals(r,data);
				count.incrementAndGet();
			}
		});
		in.push(a);
		assertEquals(calls,count.get());
	}

	public <T> void assertBif(final Operator op, final T a, final T b, final T r, int calls) {
		Source in = Beam.source();
		Beam sut = op.make(b);
		Sink out = Beam.sink();
		in.pipe(sut).pipe(out);
		final AtomicLong count = new AtomicLong();
		out.on("data", new Action<T>() {
			public void onData(T data) {
				assertEquals(r,data);
				count.incrementAndGet();
			}
		});
		in.push(a);
		assertEquals(calls,count.get());
	}
	
	@Test
	public void builtinFunctions() {
		int DROP = 666;
		assertBif(IntBifs.band, 0x0F, 0xF0, 0, 1);
		assertBif(IntBifs.bor, 0xF0, 0xFF, 0xFF, 1);
		assertBif(IntBifs.bnot, 0, -1, 1);
		assertBif(IntBifs.bxor, 0xF0, 0xFF, 0x0F, 1);
		assertBif(IntBifs.bls, 0x0F, 4, 0xF0, 1);
		assertBif(IntBifs.brs, 0xF0, 4, 0x0F, 1);
		assertBif(IntBifs.bzrs, 0xF0, 4, 0x0F, 1);
		assertBif(IntBifs.inc, 1, 2, 1);
		assertBif(IntBifs.dec, 2, 1, 1);
		assertBif(IntBifs.plus, 4, 2, 6, 1);
		assertBif(IntBifs.minus, 4, 2, 2, 1);
		assertBif(IntBifs.mul, 4, 2, 8, 1);
		assertBif(IntBifs.div, 4, 2, 2, 1);
		assertBif(IntBifs.mod, 4, 2, 0, 1);
		assertBif(IntBifs.neg, 4, -4, 1);
		assertBif(IntBifs.gt, 4, 2, 4, 1);
		assertBif(IntBifs.gt, 2, 4, 0, 0);
		assertBif(IntBifs.gte, 4, 4, 4, 1);
		assertBif(IntBifs.gte, 2, 4, DROP, 0);
		assertBif(IntBifs.lt, 1, 2, 1, 1);
		assertBif(IntBifs.lt, 2, 1, DROP, 0);
		assertBif(IntBifs.lte, 2, 4, 2, 1);
		assertBif(IntBifs.lte, 2, 1, DROP, 0);
		assertBif(IntBifs.eq, 1, 1, 1, 1);
		assertBif(IntBifs.eq, 1, 2, DROP, 0);
		assertBif(IntBifs.ne, 2, 4, 2, 1);
		assertBif(IntBifs.ne, 2, 2, DROP, 0);
		assertBif(LongBifs.band, 0x0FL, 0xF0L, 0L, 1);
		assertBif(LongBifs.bor, 0xF0L, 0xFFL, 0xFFL, 1);
		assertBif(LongBifs.bnot, 0L, -1L, 1);
		assertBif(LongBifs.bxor, 0xF0L, 0xFFL, 0x0FL, 1);
		assertBif(LongBifs.bls, 0x0FL, 4L, 0xF0L, 1);
		assertBif(LongBifs.brs, 0xF0L, 4L, 0x0FL, 1);
		assertBif(LongBifs.bzrs, 0xF0L, 4L, 0x0FL, 1);
		assertBif(LongBifs.inc, 1L, 2L, 1);
		assertBif(LongBifs.dec, 2L, 1L, 1);
		assertBif(LongBifs.plus, 4L, 2L, 6L, 1);
		assertBif(LongBifs.minus, 4L, 2L, 2L, 1);
		assertBif(LongBifs.mul, 4L, 2L, 8L, 1);
		assertBif(LongBifs.div, 4L, 2L, 2L, 1);
		assertBif(LongBifs.mod, 4L, 2L, 0L, 1);
		assertBif(LongBifs.neg, 4L, -4L, 1);
		assertBif(LongBifs.gt, 4L, 2L, 4L, 1);
		assertBif(LongBifs.gt, 2L, 4L, 0L, 0);
		assertBif(LongBifs.gte, 4L, 4L, 4L, 1);
		assertBif(LongBifs.gte, 2L, 4L, DROP, 0);
		assertBif(LongBifs.lt, 1L, 2L, 1L, 1);
		assertBif(LongBifs.lt, 2L, 1L, DROP, 0);
		assertBif(LongBifs.lte, 2L, 4L, 2L, 1);
		assertBif(LongBifs.lte, 2L, 1L, DROP, 0);
		assertBif(LongBifs.eq, 1L, 1L, 1L, 1);
		assertBif(LongBifs.eq, 1L, 2L, DROP, 0);
		assertBif(LongBifs.ne, 2L, 4L, 2L, 1);
		assertBif(LongBifs.ne, 2L, 2L, DROP, 0);
	}
	
}

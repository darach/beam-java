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

import java.util.Arrays;

public abstract class Beam extends EventEmitter {
	public Object process(final Object args) {
		return args;
	}
	
	public void push(Object args) {
		emit("data", args);
	}

	public Beam pipe(final Object... args) {
		if (args.length == 0)
			throw new Error("Bad pipe. MUST have at least one argument.");

		Object[] data = (args.length == 1) ? null : Arrays.copyOfRange(args, 1, args.length-1);

		Object candidate = args[0];
		Beam op = (Beam)candidate;
		
		return _pipe(this, op, data);
	}
	
	private Beam _pipe(final Beam prior, final Beam next, final Object... args) {
		final Beam decl = next.make();
		final Action onData = new Action() {
			public void onData(Object data) {
				decl.push(decl.process(data));
			}
		};
		prior.on("data", onData);		
		
		return decl;
	}

	public abstract Beam make(Object... args);
	
	public static Source source() {
	    return new Source();
    }

	public static Sink sink() {
	    return new Sink();
    }

	public static <T> Filter filter(PredicateFunction<T> fn, T... spec) {
	    return new Filter(fn,spec);
    }

	public static <R,T> Transform transform(TypedFunction<R,T> fn, T... spec) {
		return new Transform(fn,spec);
    }
}

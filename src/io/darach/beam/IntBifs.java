// Copyright (c) 2013 Darach Ennis < darach at gmail dot com >.
//
//Permission is hereby granted, free of charge, to any person obtaining a
//copy of this software and associated documentation files (the
//"Software"), to deal in the Software without restriction, including
//without limitation the rights to use, copy, modify, merge, publish,
//distribute, sublicense, and/or sell copies of the Software, and to permit
//persons to whom the Software is furnished to do so, subject to the
//following conditions:  
//
//The above copyright notice and this permission notice shall be included
//in all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
//OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
//NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
//DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
//OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
//USE OR OTHER DEALINGS IN THE SOFTWARE.

package io.darach.beam;

public class IntBifs {
	private IntBifs() { }
	
	final static Transform plus = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args + param(0);
        }
    });

	final static Transform minus = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args - param(0);
        }
    });

	final static Transform mul = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args * param(0);
        }
    });

	final static Transform div = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args / param(0);
        }
    });
	
	final static Transform mod = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args % param(0);
        }
    });
	
	final static Transform inc = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args + 1;
        }
    });

	final static Transform dec = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args - 1;
        }
    });
	
	final static Transform neg = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return -args;
        }
    });
	
	final static Filter lt = Beam.filter(new PredicateFunction<Integer>() {
        public Boolean invoke(Integer args) {
        	return ((Integer)args) < param(0);
        }
    });
	
	final static Filter lte = Beam.filter(new PredicateFunction<Integer>() {
        public Boolean invoke(Integer args) {
        	return ((Integer)args) <= param(0);
        }
    });
	
	final static Filter gt = Beam.filter(new PredicateFunction<Integer>() {
        public Boolean invoke(Integer args) {
        	return ((Integer)args) > param(0);
        }
    });
	
	final static Filter gte = Beam.filter(new PredicateFunction<Integer>() {
        public Boolean invoke(Integer args) {
        	return args >= param(0);
        }
    });
	
	final static Filter eq = Beam.filter(new PredicateFunction<Integer>() {
        public Boolean invoke(Integer args) {
        	return args == param(0);
        }
    });
	
	final static Filter ne = Beam.filter(new PredicateFunction<Integer>() {
        public Boolean invoke(Integer args) {
        	return args != param(0);
        }
    });
	
	final static Transform band = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args & param(0);
        }
    });

	final static Transform bor = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args | param(0);
        }
    });

	final static Transform bxor = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args ^ param(0);
        }
    });

	final static Transform bnot = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return ~args;
        }
    });

	final static Transform bls = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args << param(0);
        }
    });

	final static Transform brs = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args >> param(0);
        }
    });

	final static Transform bzrs = Beam.transform(new Function<Integer>() {
        public Integer invoke(Integer args) {
        	return args >>> param(0);
        }
    });

}

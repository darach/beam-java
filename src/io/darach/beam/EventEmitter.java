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

import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The <code>EventEmitter</code> class is a minimal interpretation of a Node.js EventEmitter
 *
 */
public class EventEmitter {
	private final Map<String,Event> eventMap = new ConcurrentHashMap<String, Event>();
	
	public void emit(String eventName, Object args) {
		Event x = eventMap.get(eventName);
		if (x != null) {
			x.fireChanged();
			x.notifyObservers(args);
		}
	}
	
	public void on(String eventName, Action o) {
		Observable x = eventMap.get(eventName);

		if (x == null) {
			final Event y = new Event();
			eventMap.put(eventName, y);
		}

		eventMap.get(eventName).addObserver(o);
	}
	
	public void removeListener(String eventName, Action o) {
		if (eventMap.containsKey(eventName)) {
			eventMap.get(eventName).deleteObserver(o);
		}
	}
}

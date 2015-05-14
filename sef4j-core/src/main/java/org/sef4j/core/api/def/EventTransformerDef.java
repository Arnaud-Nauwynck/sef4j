package org.sef4j.core.api.def;

import java.util.function.Function;

import org.sef4j.core.util.factorydef.AbstractObjectDef;

public abstract class EventTransformerDef<TSrc,T> extends AbstractObjectDef {

	/** */
	private static final long serialVersionUID = 1L;

	
	// ------------------------------------------------------------------------
	
	public static class FuncEventTransformerDef<TSrc,T> extends EventTransformerDef<TSrc,T> {
	
		/** */
		private static final long serialVersionUID = 1L;
		
		private Function<TSrc,T> fransformer;
	
		public FuncEventTransformerDef(Function<TSrc, T> fransformer) {
			this.fransformer = fransformer;
		}
	
		public Function<TSrc, T> getFransformer() {
			return fransformer;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fransformer == null) ? 0 : fransformer.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			FuncEventTransformerDef<TSrc,T> other = (FuncEventTransformerDef<TSrc,T>) obj;
			if (fransformer == null) {
				if (other.fransformer != null)
					return false;
			} else if (!fransformer.equals(other.fransformer))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "FuncEventTransformerDef [fransformer=" + fransformer + "]";
		}
		
	}	
}

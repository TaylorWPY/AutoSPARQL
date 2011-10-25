package org.autosparql.shared;

import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.NestedModelUtil;
import com.extjs.gxt.ui.client.data.RpcMap;

/** Can hold more than just the properties with the get-methods (should include all triples for the resource).
 * @author originally by Lorenz Bühmann, extended by Konrad Höffner */

public class Example extends BaseModel
{
	private static final long serialVersionUID = 6955538657940009581L;

	public final static String LABEL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#label";
	public final static String IMAGE_URL = "http://xmlns.com/foaf/0.1/depiction";
	public final static String COMMENT = "http://www.w3.org/2000/01/rdf-schema#comment";

	// else there are problems with dots in the names
	protected boolean allowNestedValues = false;

	/** sorl is used as a fallback if the normal endpoint does not work */
	public boolean containsSolrData = false;

	public Example(){}

	@Override public int hashCode()
	{
		return get("uri").hashCode();
	}

	// ** COPIED OVER FROM BASEMODEL AND BASEMODELDATA ********************************************************************************
	  // copied over from BaseModelData in order to disable "allowNestedValues" (fields cannot be overridden)
	  @SuppressWarnings({"unchecked", "rawtypes"})
	  public <X> X get(String property) {
	    if (allowNestedValues && NestedModelUtil.isNestedProperty(property)) {
	      return (X) NestedModelUtil.getNestedValue(this, property);
	    }
	    if (map == null) {
	      return null;
	    }
	    int start = property.indexOf("[");
	    int end = property.indexOf("]");
	    X obj = null;
	    if (start > -1 && end > -1) {
	      Object o = map.get(property.substring(0, start));
	      String p = property.substring(start + 1, end);
	      if (o instanceof Object[]) {
	        obj = (X) ((Object[]) o)[Integer.valueOf(p)];
	      } else if (o instanceof List) {
	        obj = (X) ((List) o).get(Integer.valueOf(p));
	      } else if (o instanceof Map) {
	        obj = (X) ((Map) o).get(p);
	      }
	    } else {
	      obj = (X) map.get(property);
	    }
	    return obj;
	  }
	  
	  // copied from BaseModel
	  @Override
	  public <X> X set(String name, X value) {
	    X oldValue = setFromBaseModelData(name, value);
	    notifyPropertyChanged(name, value, oldValue);
	    return oldValue;
	  }
	  
	  @SuppressWarnings({"unchecked", "rawtypes"})
	  public <X> X setFromBaseModelData(String property, X value) {
	    if (allowNestedValues && NestedModelUtil.isNestedProperty(property)) {
	      return (X) NestedModelUtil.setNestedValue(this, property, value);
	    }
	    if (map == null) {
	      map = new RpcMap();
	    }

	    int start = property.indexOf("[");
	    int end = property.indexOf("]");

	    if (start > -1 && end > -1) {
	      Object o = get(property.substring(0, start));
	      String p = property.substring(start + 1, end);
	      if (o instanceof Object[]) {
	        int i = Integer.valueOf(p);
	        Object[] oa = (Object[]) o;
	        X old = (X) oa[i];
	        oa[i] = value;
	        return old;
	      } else if (o instanceof List) {
	        int i = Integer.valueOf(p);
	        List list = (List) o;
	        return (X) list.set(i, value);
	      } else if (o instanceof Map) {
	        Map map = (Map) o;
	        return (X) map.put(p, value);
	      } else {
	        // not supported
	        return null;
	      }
	    } else {
	      return (X) map.put(property, value);
	    }
	  }
	// ** END COPIED OVER FROM BASEMODEL AND BASEMODELDATA ****************************************************************************

	  
	@Override
	public boolean equals(Object o)
	{
		if(o!=null&&o instanceof Example) return this.equals((Example)o);
		return false;
	}

	public boolean equals(Example e)
	{
		return get("uri").toString().equals(e.get("uri").toString());
	}

	public Example(String uri, String label, String imageURL, String comment)
	{
		set("uri", uri);
		set(LABEL, label);
		set(IMAGE_URL, imageURL);
		set(COMMENT, comment);
	}


	public Example(String uri)
	{
		set("uri",uri);
	}

	public String getURI(){
		return (String)get("uri");
	}	


	public String getLabel(){
		return (String)get(LABEL);
	}

	public String getImageURL(){
		return (String)get(IMAGE_URL);
	}

	public String getComment(){
		return (String)get(COMMENT);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for(String name: this.getPropertyNames())
		{
			sb.append(name+"->"+this.getProperties().get(name));
			sb.append(',');
		}
		return sb.substring(0,sb.length()-1);
	}

}
package cucumber.eclipse.editor.tests;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class TestMarker implements IMarker {
    
    private final String type;
    
    private final IResource resource;
    
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    
    public TestMarker(String type, IResource resource) {
        this.type = type;
        this.resource = resource;
    }
    
    public Object getAttribute(String attributeName) throws CoreException {
        return attributes.get(attributeName);
    }

    public int getAttribute(String attributeName, int defaultValue) {
        Object value = attributes.get(attributeName);
        return value == null ? defaultValue : (Integer) value;
    }

    public String getAttribute(String attributeName, String defaultValue) {
        Object value = attributes.get(attributeName);
        return value == null ? defaultValue : (String) value;
    }

    public boolean getAttribute(String attributeName, boolean defaultValue) {
        Object value = attributes.get(attributeName);
        return value == null ? defaultValue : (Boolean) value;
    }

    public Map getAttributes() throws CoreException {
        return attributes;
    }

    public Object[] getAttributes(String[] attributeNames) throws CoreException {
        Object[] result = new Object[attributeNames.length];
        for (int i = 0; i < attributeNames.length; i ++) {
            result[i] = attributes.get(attributeNames[i]);
        }
        return result;
    }

    public IResource getResource() {
        return resource;
    }

    public String getType() throws CoreException {
        return type;
    }

    public void setAttribute(String attributeName, int value) throws CoreException {
        attributes.put(attributeName, value);
    }

    public void setAttribute(String attributeName, Object value) throws CoreException {
        attributes.put(attributeName, value);
    }

    public void setAttribute(String attributeName, boolean value) throws CoreException {
        attributes.put(attributeName, value);
    }

    public void setAttributes(String[] attributeNames, Object[] values) throws CoreException {
        for (int i = 0; i < attributeNames.length; i ++) {
            attributes.put(attributeNames[i], values[i]);
        }
    }

    public void setAttributes(Map attributes) throws CoreException {
        this.attributes.putAll(attributes);
    }

    // default implementations
    
    public void delete() throws CoreException {
    }

    public boolean exists() {
        return false;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    public long getCreationTime() throws CoreException {
        return 0;
    }

    public long getId() {
        return 0;
    }

    public boolean isSubtypeOf(String superType) throws CoreException {
        return false;
    }
}

/* $Id$
 *
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester2.plugins;

import java.io.IOException;
import java.util.Properties;
import java.util.List;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.digester2.Digester;
import org.apache.commons.digester2.SAXHandler;
import org.apache.commons.digester2.Context;
import org.apache.commons.digester2.plugins.strategies.*;

/**
 * Provides configuration that is per-saxhandler (or per-digester).
 */

public class PluginConfiguration {
    private static final SAXHandler.ItemId PLUGIN_CONFIGURATION_ITEM
        = new SAXHandler.ItemId(PluginConfiguration.class, "instance");

    // the xml attribute the user uses on an xml element to specify
    // the plugin's class
    public final String DFLT_PLUGIN_CLASS_ATTR_NS = "";
    public final String DFLT_PLUGIN_CLASS_ATTR = "plugin-class";

    // the xml attribute the user uses on an xml element to specify
    // the plugin's class
    public final String DFLT_PLUGIN_ID_ATTR_NS = "";
    public final String DFLT_PLUGIN_ID_ATTR = "plugin-id";

    /** See {@link #setPluginClassAttribute}. */
    private String pluginClassAttrNS = DFLT_PLUGIN_CLASS_ATTR_NS;

    /** See {@link #setPluginClassAttribute}. */
    private String pluginClassAttr = DFLT_PLUGIN_CLASS_ATTR;

    /** See {@link #setPluginClassAttribute}. */
    private String pluginIdAttrNS = DFLT_PLUGIN_ID_ATTR_NS;

    /** See {@link #setPluginClassAttribute}. */
    private String pluginIdAttr = DFLT_PLUGIN_ID_ATTR;

    /**
     * A list of RuleFinder objects used by all Declarations (and thus
     * indirectly by all PluginCreateAction instances to locate the custom
     * rules for plugin classes.
     */
    private List ruleFinders;

    //------------------- constructors ---------------------------------------

    public static PluginConfiguration getInstance(Digester digester) {
        return getInstance(digester.getSAXHandler());
    }

    public static PluginConfiguration getInstance(Context context) {
        return getInstance(context.getSAXHandler());
    }

    public static PluginConfiguration getInstance(SAXHandler saxHandler) {
        PluginConfiguration pc =
            (PluginConfiguration) saxHandler.getItem(PLUGIN_CONFIGURATION_ITEM);

        if (pc == null) {
            pc = new PluginConfiguration();
            saxHandler.putItem(PLUGIN_CONFIGURATION_ITEM, pc);
        }

        return pc;
    }

    //------------------- methods ---------------------------------------

    /**
     * Return the list of RuleFinder objects. Under normal circumstances
     * this method creates a default list of these objects when first called
     * (ie "on-demand" or "lazy initialization"). However if setRuleFinders
     * has been called first, then the list specified there is returned.
     * <p>
     * It is explicitly permitted for the caller to modify this list
     * by inserting or removing RuleFinder objects.
     */
    public List getRuleFinders() {
        if (ruleFinders == null) {
            // when processing a plugin declaration, attempts are made to
            // find custom rules in the order in which the Finder objects
            // are added below. However this list can be modified
            ruleFinders = new LinkedList();
            //ruleFinders.add(new FinderFromFile());
            //ruleFinders.add(new FinderFromResource());
            ruleFinders.add(new FinderFromClass());
            ruleFinders.add(new FinderFromMethod());
            ruleFinders.add(new FinderFromDfltMethod());
            ruleFinders.add(new FinderFromDfltClass());
            //ruleFinders.add(new FinderFromDfltResource());
            //ruleFinders.add(new FinderFromDfltResource(".xml"));
            ruleFinders.add(new FinderSetProperties());
        }
        return ruleFinders;
    }

    /**
     * Set the list of RuleFinder objects. This may be useful if working
     * in a non-english language, allowing the application developer to
     * replace the standard list with a list of objects which look for xml
     * attributes in the local language.
     * <p>
     * If the intent is just to add an additional rule-finding algorithm, then
     * it may be better to call #getRuleFinders, and insert a new object into
     * the start of the list.
     */
    public void setRuleFinders(List ruleFinders) {
        this.ruleFinders = ruleFinders;
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a
     * PluginCreateRule which class should be instantiated.
     * <p>
     * Example:
     * <pre>
     * setPluginClassAttribute(null, "class");
     * </pre>
     * will allow this in the input xml:
     * <pre>
     *  &lt;root&gt;
     *    &lt;some-plugin class="com.acme.widget"&gt; ......
     * </pre>
     * instead of the default syntax:
     * <pre>
     *  &lt;root&gt;
     *    &lt;some-plugin plugin-class="com.acme.widget"&gt; ......
     * </pre>
     * This is particularly useful if the input xml document is not in
     * English.
     * <p>
     * Note that the xml attributes used by PluginDeclarationRules are not
     * affected by this method.
     *
     * @param namespaceURI is the namespace uri that the specified attribute
     * is in. If the attribute is in no namespace, then this should be null.
     * Note that if a namespace is used, the attrName value should <i>not</i>
     * contain any kind of namespace-prefix. Note also that if you are using
     * a non-namespace-aware parser, this parameter <i>must</i> be null.
     *
     * @param attrName is the attribute whose value contains the name of the
     * class to be instantiated.
     */
    public void setPluginClassAttribute(String namespaceURI, String attrName) {
        if (namespaceURI == null) {
            // The org.xml.sax.Attributes.getValue method expects an empty
            // string, not null, to be used to indicate no namespace.
            namespaceURI = "";
        }
        pluginClassAttrNS = namespaceURI;
        pluginClassAttr = attrName;
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a
     * PluginCreateRule which plugin declaration is being referenced.
     * <p>
     * Example:
     * <pre>
     * setPluginIdAttribute(null, "id");
     * </pre>
     * will allow this in the input xml:
     * <pre>
     *  &lt;root&gt;
     *    &lt;some-plugin id="widget"&gt; ......
     * </pre>
     * rather than the default behaviour:
     * <pre>
     *  &lt;root&gt;
     *    &lt;some-plugin plugin-id="widget"&gt; ......
     * </pre>
     * This is particularly useful if the input xml document is not in
     * English.
     * <p>
     * Note that the xml attributes used by PluginDeclarationRules are not
     * affected by this method.
     *
     * @param namespaceURI is the namespace uri that the specified attribute
     * is in. If the attribute is in no namespace, then this should be null.
     * Note that if a namespace is used, the attrName value should <i>not</i>
     * contain any kind of namespace-prefix. Note also that if you are using
     * a non-namespace-aware parser, this parameter <i>must</i> be null.
     *
     * @param attrName is the attribute whose value contains the id of the
     * plugin declaration to be used when instantiating an object.
     */
    public void setPluginIdAttribute(String namespaceURI, String attrName) {
        if (namespaceURI == null) {
            // The org.xml.sax.Attributes.getValue method expects an empty
            // string, not null, to be used to indicate no namespace.
            namespaceURI = "";
        }
        pluginIdAttrNS = namespaceURI;
        pluginIdAttr = attrName;
    }

    /**
     * Get the namespace for the xml attribute which indicates to a
     * PluginCreateRule which class is to be plugged in.
     * <p>
     * May be null (in fact, normally will be).
     */
    public String getPluginClassAttrNS() {
        return pluginClassAttrNS;
    }

    /**
     * Get the namespace for the xml attribute which indicates to a
     * PluginCreateRule which class is to be plugged in.
     * <p>
     * The return value is never null.
     */
    public String getPluginClassAttr() {
        return pluginClassAttr;
    }

    /**
     * Get the namespace for the xml attribute which indicates to a
     * PluginCreateRule which previous plugin declaration should be used.
     * <p>
     * May be null (in fact, normally will be).
     */
    public String getPluginIdAttrNS() {
        return pluginIdAttrNS;
    }

    /**
     * Get the namespace for the xml attribute which indicates to a
     * PluginCreateRule which previous plugin declaration should be used.
     * <p>
     * The return value is never null.
     */
    public String getPluginIdAttr() {
        return pluginIdAttr;
    }
}
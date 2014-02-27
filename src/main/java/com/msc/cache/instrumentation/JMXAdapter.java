package com.msc.cache.instrumentation;

import javax.management.*;
import java.util.ArrayList;
import java.util.logging.Logger;


/**
 * This class provides some helper facilities to register MBeans under CacheMBeans platform.
 *
 * @errorMessage error.platform.base.instrumentation.unableToRegisterMBean=Unable to register MBean {0}
 * <p/>
 * This code was written by Marcelo Caldas.
 * e-Mail: mscaldas@gmail.com
 * <p/>
 * \* Project: QuickCache
 * <p/>
 * Date: 2/25/14
 * <p/>
 * Enjoy the details of life.
 */
public class JMXAdapter {
    private static final Logger logger = Logger.getLogger(JMXAdapter.class.getName());
    /**
     * The name of the MBeanServer Domain that will contain all LHC specific
     * MBeans.
     * This domain can be combined with the subdomain for better separation
     * of mbeans being deployed.
     */

    private static final String UNABLE_TO_REGISTER_MBEAN = "error.platform.base.instrumentation.unableToRegisterMBean";
    /**
     * Reference to the MBeanServer.
     */
    protected MBeanServer server;
    /**
     * A subDomain for better division of mbeans being deployed under CacheMBeans
     */
    protected String subDomain;

    /**
     * Default Constructor which initializes the MBean server.
     */
    public JMXAdapter() {
        this.createMBeanServer();
    }

    /**
     * constructor that receives a subDomain as parameter.
     *
     * @param subDomain The subdomain to use while registering instances under this instance.
     */
    public JMXAdapter(String subDomain) {
        this.subDomain = subDomain;
        this.createMBeanServer();
    }

    /**
     * Getter method for the subDomain property
     *
     * @return The current value assigned to subDomain property.
     */
    public String getSubDomain() {
        return subDomain;
    }

    /**
     * Setter method for subDomain property.
     *
     * @param subDomain The new value to be assigned to subDomain property.
     */
    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    /**
     * Getter method for the Full Domain (CacheMBeans + subdomain)
     * for object name registration.
     *
     * @return The full domain specified by CacheMBeans and subDomain.
     */
    public String getDomain() {
        String domain = "CacheMBeans";
        if (this.getSubDomain() != null && this.getSubDomain().trim().length() > 0) {
            domain += "." + this.getSubDomain();
        }
        return domain;
    }

    /**
     * Create MBean Server with the full domain name, if not already done so,
     * and associates with this class.
     * This implementation uses the <code>MBeanServerFactory.findMBeanServer</code>
     * method to determine if there are an MBeanServer's in the JVM.  If there exists
     * more than one, then this method associates this class with the first one
     * returned by the <code>findMBeanServer</code> method.  There is
     * no documentation at the time of this writing that indicates the ordering
     * of MBeanServers returned by this method, so the ordering should be
     * considered indeterminate.  Regardless, at present, when implemented with
     * JBoss, this does not appear to cause a problem.
     */
    protected void createMBeanServer() {
        final String METHOD = "createMBeanServer^^ ";
        if (this.server == null) {
            //ArrayList servers = MBeanServerFactory.findMBeanServer(MBeanHome.ADMIN_JNDI_NAME);
            ArrayList servers = MBeanServerFactory.findMBeanServer(getDomain());
            if (servers.size() > 0) {
                this.server = (MBeanServer) servers.get(0);
                logger.finest("Found server: " + this.server.getDefaultDomain() + " # of MBeans: " + this.server.getMBeanCount());
            } else {
                this.server = MBeanServerFactory.createMBeanServer(this.getDomain());
                logger.finest("Creating server: " + this.server.getDefaultDomain() + " # of MBeans: " + this.server.getMBeanCount());
            }
        }

        logger.finest(METHOD + "Create MBeanServer successfully");
    }

    /**
     * Creates an ObjectName using the full domain.
     *
     * @param str String representing the ObjectName to be created
     * @return the new created ObjectName
     */
    protected ObjectName createObjectName(String str) throws MalformedObjectNameException {
        return new ObjectName(this.getDomain() + ":name=" + str);
    }

    /**
     * Registers an MBean with the full domain using the name and class provided.
     *
     * @param mbeanName     the Object Name (in the JMX sense) to associate with the MBean being registered.
     * @param mbeanInstance A instance of a mbean that is being registered.
     */
    public void registerMBean(String mbeanName, Object mbeanInstance) throws Exception {
        try {
            ObjectName objName;
            objName = this.createObjectName(mbeanName);
            if (this.server.isRegistered(objName)) {
                this.server.unregisterMBean(objName);
            }
            this.server.registerMBean(mbeanInstance, objName);
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException | InstanceNotFoundException e) {
            throw new Exception("Unable to register mbean " + mbeanName + "\n" + e.getMessage(), e);
        }
    }
}




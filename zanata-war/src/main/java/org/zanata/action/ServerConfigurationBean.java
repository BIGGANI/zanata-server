/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.action;

import java.io.Serializable;

import org.hibernate.validator.Email;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.zanata.ApplicationConfiguration;
import org.zanata.action.validator.EmailList;
import org.zanata.dao.ApplicationConfigurationDAO;
import org.zanata.model.HApplicationConfiguration;
import org.zanata.model.validator.UrlNoSlash;

@Name("serverConfigurationBean")
@Scope(ScopeType.PAGE)
@Restrict("#{s:hasRole('admin')}")
public class ServerConfigurationBean implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   @Logger
   Log log;

   @In
   ApplicationConfigurationDAO applicationConfigurationDAO;

   private String registerUrl;
   private String serverUrl;
   private String emailDomain;
   private String adminEmail;
   private String fromEmailAddr;
   private String homeContent;
   private String helpContent;
   private boolean enableLogEmail;
   private String logDestinationEmails;
   private String logEmailLevel;


   public String getHomeContent()
   {
      HApplicationConfiguration var = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_HOME_CONTENT);
      return var != null ? var.getValue() : "";
   }

   public void setHomeContent(String homeContent)
   {
      this.homeContent = homeContent;
   }

   public String getHelpContent()
   {
      HApplicationConfiguration var = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_HELP_CONTENT);
      return var != null ? var.getValue() : "";
   }

   public void setHelpContent(String helpContent)
   {
      this.helpContent = helpContent;
   }

   @EmailList
   public String getAdminEmail()
   {
      return adminEmail;
   }

   public void setAdminEmail(String adminEmail)
   {
      this.adminEmail = adminEmail;
   }

   @Email
   public String getFromEmailAddr()
   {
      return fromEmailAddr;
   }

   public void setFromEmailAddr(String fromEmailAddr)
   {
      this.fromEmailAddr = fromEmailAddr;
   }

   public String getEmailDomain()
   {
      return emailDomain;
   }

   public void setEmailDomain(String emailDomain)
   {
      this.emailDomain = emailDomain;
   }

   public String updateHomeContent()
   {
      HApplicationConfiguration var = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_HOME_CONTENT);
      if (var != null)
      {
         if (homeContent == null || homeContent.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(var);
         }
         else
         {
            var.setValue(homeContent);
         }
      }
      else if (homeContent != null && !homeContent.isEmpty())
      {
         HApplicationConfiguration op = new HApplicationConfiguration(HApplicationConfiguration.KEY_HOME_CONTENT, homeContent);
         applicationConfigurationDAO.makePersistent(op);
      }
      applicationConfigurationDAO.flush();
      FacesMessages.instance().add("Home content was successfully updated.");
      if (Events.exists())
      {
         Events.instance().raiseTransactionSuccessEvent(ApplicationConfiguration.EVENT_CONFIGURATION_CHANGED);
      }
      return "/home.xhtml";
   }

   public String updateHelpContent()
   {
      HApplicationConfiguration var = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_HELP_CONTENT);
      if (var != null)
      {
         if (helpContent == null || helpContent.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(var);
         }
         else
         {
            var.setValue(helpContent);
         }
      }
      else if (helpContent != null && !helpContent.isEmpty())
      {
         HApplicationConfiguration op = new HApplicationConfiguration(HApplicationConfiguration.KEY_HELP_CONTENT, helpContent);
         applicationConfigurationDAO.makePersistent(op);
      }
      applicationConfigurationDAO.flush();
      FacesMessages.instance().add("Help page content was successfully updated.");
      if (Events.exists())
      {
         Events.instance().raiseTransactionSuccessEvent(ApplicationConfiguration.EVENT_CONFIGURATION_CHANGED);
      }
      return "/help/view.xhtml";
   }


   public String getRegisterUrl()
   {
      return registerUrl;
   }

   public void setRegisterUrl(String registerUrl)
   {
      this.registerUrl = registerUrl;
   }

   @UrlNoSlash
   public String getServerUrl()
   {
      return serverUrl;
   }

   public void setServerUrl(String serverUrl)
   {
      this.serverUrl = serverUrl;
   }

   public boolean isEnableLogEmail()
   {
      return enableLogEmail;
   }

   public void setEnableLogEmail(boolean enableLogEmail)
   {
      this.enableLogEmail = enableLogEmail;
   }

   public String getLogDestinationEmails()
   {
      return logDestinationEmails;
   }

   public void setLogDestinationEmails(String logDestinationEmails)
   {
      this.logDestinationEmails = logDestinationEmails;
   }

   public String getLogEmailLevel()
   {
      return logEmailLevel;
   }

   public void setLogEmailLevel(String logEmailLevel)
   {
      this.logEmailLevel = logEmailLevel;
   }

   @Create
   public void onCreate()
   {
      HApplicationConfiguration registerUrlValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_REGISTER);
      if (registerUrlValue != null)
      {
         this.registerUrl = registerUrlValue.getValue();
      }
      HApplicationConfiguration serverUrlValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_HOST);
      if (serverUrlValue != null)
      {
         this.serverUrl = serverUrlValue.getValue();
      }
      HApplicationConfiguration emailDomainValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_DOMAIN);
      if (emailDomainValue != null)
      {
         this.emailDomain = emailDomainValue.getValue();
      }
      HApplicationConfiguration adminEmailValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_ADMIN_EMAIL);
      if (adminEmailValue != null)
      {
         this.adminEmail = adminEmailValue.getValue();
      }
      HApplicationConfiguration fromAddressValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_EMAIL_FROM_ADDRESS);
      if (fromAddressValue != null)
      {
         this.fromEmailAddr = fromAddressValue.getValue();
      }
      HApplicationConfiguration emailLogEventsValue  = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_EMAIL_LOG_EVENTS);
      if (emailLogEventsValue != null)
      {
         this.enableLogEmail = Boolean.parseBoolean(emailLogEventsValue.getValue());
      }
      HApplicationConfiguration logDestinationValue  = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_LOG_DESTINATION_EMAIL);
      if (logDestinationValue != null)
      {
         this.logDestinationEmails = logDestinationValue.getValue();
      }
      HApplicationConfiguration logEmailLevelValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_EMAIL_LOG_LEVEL);
      if(logEmailLevelValue != null)
      {
         this.logEmailLevel = logEmailLevelValue.getValue();
      }
   }

   @Transactional
   public void update()
   {
      HApplicationConfiguration registerUrlValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_REGISTER);
      if (registerUrlValue != null)
      {
         if (registerUrl == null || registerUrl.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(registerUrlValue);
         }
         else
         {
            registerUrlValue.setValue(registerUrl);
         }
      }
      else if (registerUrl != null && !registerUrl.isEmpty())
      {
         registerUrlValue = new HApplicationConfiguration(HApplicationConfiguration.KEY_REGISTER, registerUrl);
         applicationConfigurationDAO.makePersistent(registerUrlValue);
      }

      HApplicationConfiguration serverUrlValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_HOST);
      if (serverUrlValue != null)
      {
         if (serverUrl == null || serverUrl.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(serverUrlValue);
         }
         else
         {
            serverUrlValue.setValue(serverUrl);
         }
      }
      else if (serverUrl != null && !serverUrl.isEmpty())
      {
         serverUrlValue = new HApplicationConfiguration(HApplicationConfiguration.KEY_HOST, serverUrl);
         applicationConfigurationDAO.makePersistent(serverUrlValue);
      }

      HApplicationConfiguration emailDomainValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_DOMAIN);
      if (emailDomainValue != null)
      {
         if (emailDomain == null || emailDomain.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(emailDomainValue);
         }
         else
         {
            emailDomainValue.setValue(emailDomain);
         }
      }
      else if (emailDomain != null && !emailDomain.isEmpty())
      {
         emailDomainValue = new HApplicationConfiguration(HApplicationConfiguration.KEY_DOMAIN, emailDomain);
         applicationConfigurationDAO.makePersistent(emailDomainValue);
      }

      HApplicationConfiguration adminEmailValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_ADMIN_EMAIL);
      if (adminEmailValue != null)
      {
         if (adminEmail == null || adminEmail.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(adminEmailValue);
         }
         else
         {
            adminEmailValue.setValue(adminEmail);
         }
      }
      else if (adminEmail != null && !adminEmail.isEmpty())
      {
         adminEmailValue = new HApplicationConfiguration(HApplicationConfiguration.KEY_ADMIN_EMAIL, adminEmail);
         applicationConfigurationDAO.makePersistent(adminEmailValue);
      }

      HApplicationConfiguration fromEmailAddrValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_EMAIL_FROM_ADDRESS);
      if (fromEmailAddrValue != null)
      {
         if (fromEmailAddr == null || fromEmailAddr.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(fromEmailAddrValue);
         }
         else
         {
            fromEmailAddrValue.setValue(fromEmailAddr);
         }
      }
      else if (fromEmailAddr != null && !fromEmailAddr.isEmpty())
      {
         fromEmailAddrValue = new HApplicationConfiguration(HApplicationConfiguration.KEY_EMAIL_FROM_ADDRESS, fromEmailAddr);
         applicationConfigurationDAO.makePersistent(fromEmailAddrValue);
      }

      HApplicationConfiguration emailLogEventsValue  = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_EMAIL_LOG_EVENTS);
      if( emailLogEventsValue == null )
      {
         emailLogEventsValue = new HApplicationConfiguration(HApplicationConfiguration.KEY_EMAIL_LOG_EVENTS, Boolean.toString(enableLogEmail));
      }
      else
      {
         emailLogEventsValue.setValue( Boolean.toString(enableLogEmail) );
      }
      applicationConfigurationDAO.makePersistent( emailLogEventsValue );

      HApplicationConfiguration logDestEmailValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_LOG_DESTINATION_EMAIL);
      if (logDestEmailValue != null)
      {
         if (logDestinationEmails == null || logDestinationEmails.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(logDestEmailValue);
         }
         else
         {
            logDestEmailValue.setValue(logDestinationEmails);
         }
      }
      else if (logDestinationEmails != null && !logDestinationEmails.isEmpty())
      {
         logDestEmailValue = new HApplicationConfiguration(HApplicationConfiguration.KEY_LOG_DESTINATION_EMAIL, logDestinationEmails);
         applicationConfigurationDAO.makePersistent(logDestEmailValue);
      }

      HApplicationConfiguration logEmailLevelValue = applicationConfigurationDAO.findByKey(HApplicationConfiguration.KEY_EMAIL_LOG_LEVEL);
      if (logEmailLevelValue != null)
      {
         if (logEmailLevel == null || logEmailLevel.isEmpty())
         {
            applicationConfigurationDAO.makeTransient(logEmailLevelValue);
         }
         else
         {
            logEmailLevelValue.setValue(logEmailLevel);
         }
      }
      else if (logEmailLevel != null && !logEmailLevel.isEmpty())
      {
         logEmailLevelValue = new HApplicationConfiguration(HApplicationConfiguration.KEY_EMAIL_LOG_LEVEL, logEmailLevel);
         applicationConfigurationDAO.makePersistent(logEmailLevelValue);
      }

      applicationConfigurationDAO.flush();
      FacesMessages.instance().add("Configuration was successfully updated.");
      if (Events.exists())
      {
         Events.instance().raiseTransactionSuccessEvent(ApplicationConfiguration.EVENT_CONFIGURATION_CHANGED);
      }
   }

   public String cancel()
   {
      return "cancel";
   }
}

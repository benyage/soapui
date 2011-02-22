/*
 *  soapUI, copyright (C) 2004-2011 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.security.support;

import java.awt.Color;

import javax.swing.JProgressBar;

import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestRunner.Status;
import com.eviware.soapui.security.SecurityCheckResult;
import com.eviware.soapui.security.SecurityTest;
import com.eviware.soapui.security.SecurityTestRunContext;
import com.eviware.soapui.security.SecurityTestRunnerImpl;
import com.eviware.soapui.security.SecurityTestStepResult;
import com.eviware.soapui.security.SecurityCheckRequestResult.SecurityStatus;
import com.eviware.soapui.security.check.AbstractSecurityCheck;

/**
 * Class that keeps a JProgressBars state in sync with a SecurityTest
 * 
 * @author dragica.soldo
 */

public class ProgressBarSecurityTestAdapter
{
	private final JProgressBar progressBar;
	private final SecurityTest securityTest;
	private InternalTestRunListener internalTestRunListener;

	public ProgressBarSecurityTestAdapter( JProgressBar progressBar, SecurityTest securityTest )
	{
		this.progressBar = progressBar;
		this.securityTest = securityTest;

		internalTestRunListener = new InternalTestRunListener();
		securityTest.addSecurityTestRunListener( internalTestRunListener );
	}

	public void release()
	{
		securityTest.removeSecurityTestRunListener( internalTestRunListener );
	}

	public class InternalTestRunListener extends SecurityTestRunListenerAdapter
	{
		public void beforeRun( TestCaseRunner testRunner, SecurityTestRunContext runContext )
		{
			if( progressBar.isIndeterminate() )
				return;

			progressBar.getModel().setMaximum(
					( ( SecurityTestRunnerImpl )testRunner ).getSecurityTest().getSecurityCheckCount() );
			progressBar.setForeground( Color.GREEN.darker() );
		}

		// TODO - not sure if we should have next 2 methods here, separate checks
		// are better to monitor progress
		public void beforeStep( TestCaseRunner testRunner, SecurityTestRunContext runContext, TestStep testStep )
		{
			// if( progressBar.isIndeterminate() )
			// return;
			//
			// if( testStep != null )
			// {
			// progressBar.setString( testStep.getName() );
			// progressBar.setValue( runContext.getCurrentStepIndex() );
			// }
		}

		public void afterStep( TestCaseRunner testRunner, SecurityTestRunContext runContext, SecurityTestStepResult result )
		{
			// if( progressBar.isIndeterminate() )
			// return;
			//
			// if( result.getStatus() == SecurityStatus.FAILED )
			// {
			// progressBar.setForeground( Color.RED );
			// }
			// else if( !securityTest.getTestCase().getFailTestCaseOnErrors() )
			// {
			// progressBar.setForeground( Color.GREEN.darker() );
			// }

		}

		@Override
		public void beforeSecurityCheck( TestCaseRunner testRunner, SecurityTestRunContext runContext,
				AbstractSecurityCheck securityCheck )
		{

			if( progressBar.isIndeterminate() )
				return;

			if( securityCheck != null )
			{
				progressBar.setString( securityCheck.getTestStep().getName() );
				progressBar.setValue( runContext.getCurrentCheckOnSecurityTestIndex() );
			}
		}

		@Override
		public void afterSecurityCheck( TestCaseRunner testRunner, SecurityTestRunContext runContext,
				SecurityCheckResult securityCheckResult )
		{
			if( progressBar.isIndeterminate() )
				return;

			if( securityCheckResult.getStatus() == SecurityStatus.FAILED )
			{
				progressBar.setForeground( Color.RED );
			}
			else if( securityCheckResult.getStatus() == SecurityStatus.OK )
			{
				progressBar.setForeground( Color.GREEN.darker() );
			}

			progressBar.setValue( runContext.getCurrentCheckOnSecurityTestIndex() + 1 );
		}

		public void afterRun( TestCaseRunner testRunner, SecurityTestRunContext runContext )
		{
			if( testRunner.getStatus() == Status.FAILED )
			{
				progressBar.setForeground( Color.RED );
			}
			else if( testRunner.getStatus() == Status.FINISHED )
			{
				progressBar.setForeground( Color.GREEN.darker() );
			}

			if( progressBar.isIndeterminate() )
				return;

			if( testRunner.getStatus() == TestCaseRunner.Status.FINISHED )
				progressBar.setValue( ( ( SecurityTestRunnerImpl )testRunner ).getSecurityTest().getSecurityCheckCount() );

			progressBar.setString( testRunner.getStatus().toString() );
		}
	}
}

# DST-Framework
A testing framework for distributed systems

A fully documented getting started tutorial will follow within the next weeks... 

##ATTENTION
The framework in its current state has quite a few bugs, given the fact that it was developed within in 1 month as part of a bachelor thesis. That's why I highly recommend not using this framwork in productive environments. Use it at your own risk and do not hold me responsible if anything breaks. See also license below. 

## Framework Structure 
![image](https://github.com/le1nux/DST-Framework/blob/master/overview/modules_overview.png?raw=true)

The framework supports two pre-defined tests and two dummy tests
 - REST API test using the standard HTTP Methods (GET, PUT, POST, DELETE)
 - SQL test 
 - (Hello World test)
 - Sleep test)
and is fully customizable. You can use our pre-definded tests and configure them or you can even develop your own tests. You can look into the dummy tests to get a taste of how tests work.

Please contact me if you are interested in the more advanced Android test (shown in the diagram above) so I can send it to you.

## Getting Started

clone the repository e.g.
	git clone git@github.com:le1nux/DST-Framework.git
	
switch to the ant build-scripts
	cd DST-Framework/TestingFramework/build-scripts/ant

start the server
	ant -buildfile server_build.xml run

start the schedule runners
	ant -buildfile scheduleRunner_build.xml run
The schedule runner automatically connects to the server and waits for further instructions (e.g. what tests to perform and when). 

Now its time to configure the server and start a test run. When we started our server, the server started another HTTP Tomcat server listening on port 8080 as a background thread. When we want to configure and perform our tests we always interact with the Tomcat server by communicating with its REST-API. We'll see how that works in the next few steps. 

At first we send a list of the tests to perform including their parameters. 
	PUT http://127.0.0.1:8080/api/schedulestorage
Payload:
	{
		"schedules" : [ "java.util.ArrayList", [ {
    			"schedule" : [ "java.util.ArrayList", [
 			{
				"testKey" : "com.lue.client.tests.SleepTest",
				"parameters" : [ "com.lue.client.tests.SleepTestParameters", {
					"duration" : 3000
				} ]
			}] ]
		}]]
	}

I know that this part is a little bit technical but the framework needed the variables types up front otherwise I couldn't parse them appropiately. 
Let me get through this real quick: We can set up different schedules. Each schedule has a collections of tests. Later on we can decide which schedule to run (I think this still needs to be implemented). In the example above we only set up one schedule which included one test (SleepTest). Each test is identified by a testkey (package + class name) and has a list of parameters. In our case we only have the parameter duration which will make the test go to sleep for 3000ms while performing. 

After we have sent the schedule to the server the server went from state UNINITIALIZED to INITILIAZED which means we can run our test now. To do that we send the following request to the server:
	PUT http://127.0.0.1:8080/api/schedulerstate
Payload:	
	"running" 
The server will send the tests to its schedule runners which in return will perform the tests. Afterwards they send the results back to the server which will evaluate the result and print a summary to the console. 

If you want to rerun the schedule or want run a different one put the server to state UNITIALIZED and send the respective schedule to the server. 

	PUT http://127.0.0.1:8080/api/schedulerstate
Payload:	
	"UNINITIALIZED" 

Then set the server state to running.


##License
Copyright (c) 2015, le1nux
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

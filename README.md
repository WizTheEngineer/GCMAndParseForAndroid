# GCMAndParseForAndroid
Hi everyone, this project will basically get you started with Google Cloud Messenging and Parse to send real-time push notifications. This will be great if you need any type of live events such as instant messenging. It requires a bit of setup but it is pretty straight forward. If you have any question please leave it as a question on here and I will get back to you when I can. Enjoy!

<h2>Parse Setup</h2>
<ol>
  <li>Signup for <a href="http://parse.com">Parse</a> and go through the steps to create an application.</li>
  <li>Populate the ParseConfig.class file with your Parse Application ID & Client Key</li>
  <li>Create a new Cloud Code instance for your application following using the Parse <a href="https://www.parse.com/apps/quickstart">quickstart</a>.</li>
  <li>Upload the code in the main.js file to the cloud code instance you created. It's file path in the directory is parse/cloud/main.js. You can just copy and paste the code from here and add it to your cloud code main.js file.</li>
</ol>
</br>
<h2>Google Cloud Messenger Setup</h2>
<ol>
    <li>Go through the steps to setup GCM in your application <a href="https://goo.gl/uWvVT9">here.</a></li> 
    <li>Populate the GCMConfig.class file with your GCM Server API Key & Sender ID</li>
</ol>



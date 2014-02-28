<?xml version="1.0" encoding="UTF-8"?>
<!-- JBoss, Home of Professional Open Source Copyright 2014, Red Hat, Inc.
    and/or its affiliates, and individual contributors by the @authors tag. See
    the copyright.txt in the distribution for a full listing of individual contributors.
    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy
    of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required
    by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
    OF ANY KIND, either express or implied. See the License for the specific
    language governing permissions and limitations under the License. -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:preserve-space elements="exception" />

<xsl:template name="all" match="/">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>Arquillian test run report</title>
		<style type="text/css">
			body { margin: 1em; padding: 0; }
			h1 { font-size: 300%; margin: 0; padding: 0; }
			h2 { font-size: 275%; margin: 0; padding: 0; }
			h3 { font-size: 250%; margin: 0; padding: 0; }
			h4 { font-size: 200%; margin: 0; padding: 0; }
			h5 { font-size: 150%; margin: 0; padding: 0; }
			h6 { font-size: 125%; margin: 0; padding: 0; }
			.clear { clear: both; }
			.extensions { margin: 0 0 0 2em; }
			.extension { margin: 0 0 0 2em; }
			.suite { margin: 0 0 0 2em; }
			.containers { margin: 0 0 0 2em; }
			.container { margin: 0 0 0 2em; }
			.deployments { margin: 0 0 0 2em; }
			.deployment { margin: 0 0 0 2em; }
			.testClasses { margin: 0 0 0 2em; }
			.testClass { margin: 0 0 0 2em; }
			.testMethod { margin: 0 0 0 2em; }
			.testMethod > table { margin: 0 0 0 2em; }
			.properties { margin: 0 0 0 2em; }
			.properties > table { margin: 0 0 0 2em; }
			.video { margin: 0 0 0 2em; }
			.video > video { margin: 0 0 0 2em; }
			.screenshotParent { margin: 0 0 0 2em; }
			.screenshotLeft { display: inline-block; float: left; }
			.screenshotLeft:nth-child(2n) { margin: 0 0 0 2em; }
			.passed { color: green; }
			.failed { color: red; }
			.skipped { color: orange; }
			.whitespaces { margin: 0 0 0 2em; white-space: pre; }
		</style>
	</head>
	<body>
		<div id="main">
			<h1>Arquillian test run report</h1>
		</div>
		<div class="properties">
			<table>
				<tbody>
				<!-- properties hooked to report -->
				<xsl:for-each select="/report/property">
					<tr><td><xsl:value-of select="key"/></td><td><xsl:value-of select="value"/></td></tr>
				</xsl:for-each>
				<xsl:for-each select="/report/file">
					<tr><td colspan="2"><xsl:value-of select="@path"/></td></tr>
				</xsl:for-each>
				</tbody>
			</table>
		</div>
		<xsl:if test="/report/extension">
		<div class="extensions">
			<xsl:for-each select="/report/extension">
			<div class="extension">
			<h5>Extension <xsl:value-of select="@qualifier"/></h5>
			<xsl:if test="configuration">
				<div class="properties">
					<table>
						<tbody>
						<xsl:for-each select="configuration/entry">
							<tr><td><xsl:value-of select="key"/></td><td><xsl:value-of select="value"/></td></tr>
						</xsl:for-each>
						</tbody>
					</table>
				</div>
			</xsl:if>
			</div>
			</xsl:for-each>
		</div>
		</xsl:if>
		<xsl:for-each select="/report/suite">
			<div class="suite">
				<h2>Suite</h2>

				<div class="properties">
					<table>
						<tbody>
							<tr>
								<td>start</td>
								<xsl:variable name="dtStart" select="@start"/>
								<td><xsl:value-of select="concat(substring($dtStart, 6, 2),'/',substring($dtStart, 9, 2),'/',substring($dtStart, 1, 4),' - ',substring($dtStart, 12, 8))"/></td>
							</tr>
							<tr>
								<td>stop</td>
								<xsl:variable name="dtEnd" select="@stop"/>
								<td><xsl:value-of select="concat(substring($dtEnd, 6, 2),'/',substring($dtEnd, 9, 2),'/',substring($dtEnd, 1, 4),' - ',substring($dtEnd, 12, 8))"/></td>
							</tr>
							<tr>
								<td>duration</td>
								<td><xsl:value-of select="@duration div 1000"/>s</td>
							</tr>
							<!-- properties hooked to suite -->
							<xsl:for-each select="property">
								<tr><td><xsl:value-of select="key"/></td><td><xsl:value-of select="value"/></td></tr>
							</xsl:for-each>
							<xsl:for-each select="file">
								<tr><td colspan="2"><xsl:value-of select="@path"/></td></tr>
							</xsl:for-each>
						</tbody>
					</table>
				</div>

				<xsl:for-each select="video">
				<div class="video">
					<video controls="">
						<source src="file://{@path}" type="video/{@type}"/>
						Your browser does not support video tag.
					</video>
				</div>
				</xsl:for-each>

				<div class="containers">
					<h3>Containers</h3>
					<xsl:for-each select="container">
					<div class="container">
						<h4><xsl:value-of select="@qualifier"/></h4>
						<div class="properties">
							<h5>Configuration</h5>
							<table>
								<tbody>
								<!-- properties hooked to container -->
								<xsl:for-each select="configuration/entry">
									<tr><td><xsl:value-of select="key"/></td><td><xsl:value-of select="value"/></td></tr>
								</xsl:for-each>
								</tbody>
							</table>
						</div>
						<div class="deployments">
							<h5>Deployments</h5>
							<xsl:for-each select="deployment">
								<div class="deployment">
									<table>
										<tbody>
											<tr><td>name</td><td><xsl:value-of select="@name"/></td></tr>
											<tr><td>archive</td><td><xsl:value-of select="archive"/></td></tr>
											<tr><td>order</td><td><xsl:value-of select="order"/></td></tr>
											<xsl:if test="protocol != '_DEFAULT_'"><tr><td>protocol</td><td><xsl:value-of select="protocol"/></td></tr></xsl:if>
										</tbody>
									</table>
								</div>
							</xsl:for-each>	
						</div>
					</div>
					</xsl:for-each>
				</div>

				<div class="testClasses">
					<h3>Tests</h3>
					<xsl:for-each select="class">
					<div class="testClass">
						<h4><xsl:value-of select="@name"/> - <xsl:value-of select='@duration div 1000'/>s <xsl:if test="@runAsClient = 'true'"> (runs as client)</xsl:if></h4>
						<div class="properties">
							<table>
								<tbody>
									<xsl:if test="method[@result = 'PASSED']">
										<tr><td>passed</td><td><xsl:value-of select='count(method[@result = "PASSED"])'/></td></tr>
									</xsl:if>
									<xsl:if test="method[@result = 'FAILED']">
										<tr><td>failed</td><td><xsl:value-of select='count(method[@result = "FAILED"])'/></td></tr>
									</xsl:if>
									<!-- properties hooked to class -->
									<xsl:for-each select="property">
										<tr><td><xsl:value-of select="key"/></td><td><xsl:value-of select="value"/></td></tr>
									</xsl:for-each>
									<xsl:for-each select="file">
										<tr><td colspan="2"><xsl:value-of select="@path"/></td></tr>
									</xsl:for-each>
								</tbody>
							</table>
						</div>

						<xsl:for-each select="video">
						<div class="video">
							<video controls="">
								<source src="file://{@path}" type="video/{@type}"/>
								Your browser does not support video tag.
							</video>
						</div>
						</xsl:for-each>

						<xsl:for-each select="method">
							<div class="testMethod">
							<xsl:choose>
								<xsl:when test="@result = 'PASSED'">
									<h5 class="passed"><xsl:value-of select="@name"/> (<xsl:value-of select='@duration div 1000'/>s)</h5>
								</xsl:when>
								<xsl:when test="@result = 'FAILED'">
									<h5 class="failed"><xsl:value-of select="@name"/> (<xsl:value-of select='@duration div 1000'/>s)</h5>
								</xsl:when>
							</xsl:choose>
								<div class="properties">
									<table>
										<tbody>
											<xsl:if test="@runAsClient = 'true'"><tr><td colspan="2">runs as client</td></tr></xsl:if>
											<xsl:if test="@operateOnDeployment != '_DEFAULT_'"><tr><td>operates on deployment</td><td><xsl:value-of select="@operateOnDeployment"/></td></tr></xsl:if>
											<!-- properties hooked to method -->
											<xsl:for-each select="property">
												<tr><td><xsl:value-of select="key"/></td><td><xsl:value-of select="value"/></td></tr>
											</xsl:for-each>
											<xsl:for-each select="file">
												<tr><td colspan="2"><xsl:value-of select="@path"/></td></tr>
											</xsl:for-each>
										</tbody>
									</table>
									<xsl:if test="exception">
									<div class="whitespaces">
										<xsl:value-of select="exception"/>
									</div>
									</xsl:if>
								</div>

								<xsl:for-each select="video">
								<div class="video">
									<video controls="">
										<source src="file://{@path}" type="video/{@type}"/>
										Your browser does not support video tag.
									</video>
								</div>
								</xsl:for-each>

								<xsl:if test="screenshot">
								<div class="screenshotParent">
									<xsl:for-each select="screenshot">
										<div class="screenshotLeft">
										<xsl:choose>
											<xsl:when test="@phase = 'BEFORE'">
												<h6>Before</h6>
											</xsl:when>
											<xsl:when test="@phase = 'AFTER'">
												<h6>After</h6>
											</xsl:when>
											<xsl:when test="@phase = 'FAILED'">
												<h6>Failed</h6>
											</xsl:when>
										</xsl:choose>
										<img src="file://{@path}" />
										</div>
									</xsl:for-each>
									<div class="clear"></div>
								</div>
								</xsl:if>
							</div>
						</xsl:for-each>
					</div>
					</xsl:for-each>
				</div>
			</div>
		</xsl:for-each>
	</body>
</html>
</xsl:template>
</xsl:stylesheet>

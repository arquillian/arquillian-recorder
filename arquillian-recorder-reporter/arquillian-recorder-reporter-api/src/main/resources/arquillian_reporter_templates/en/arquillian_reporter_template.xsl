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

<!-- Arquillian name and logo encoded in this template are registered trademarks of Red Hat Inc. -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:preserve-space elements="exception" />

<xsl:template match="table">
    <table>
        <thead>
            <xsl:for-each select="thead/row">
                <tr>
                    <xsl:for-each select="cell"><th><xsl:value-of select="."/></th></xsl:for-each>
                </tr>
            </xsl:for-each>
        </thead>
        <tbody>
            <xsl:for-each select="tbody/row">
            <tr>
                <xsl:for-each select="cell">
                <td>
                    <xsl:attribute name="colspan"><xsl:value-of select='@colspan'/></xsl:attribute>
                    <xsl:attribute name="rowspan"><xsl:value-of select='@rowspan'/></xsl:attribute>
                    <xsl:value-of select="."/>
                </td>
                </xsl:for-each>
            </tr>
            </xsl:for-each>
        </tbody>
        <tfoot>
            <xsl:for-each select="tfoot/row">
            <tr>
                <xsl:for-each select="cell"><td><xsl:value-of select="."/></td></xsl:for-each>
            </tr>
            </xsl:for-each>
        </tfoot>
    </table>
</xsl:template>

<xsl:template match="group">
    <div class="properties">
        <h5><xsl:value-of select="@name"/></h5>

        <xsl:if test="property">
            <table>
                <tbody>
                    <xsl:for-each select="property">
                        <tr><td><xsl:value-of select="key"/></td><td><xsl:value-of select="value"/></td></tr>
                    </xsl:for-each>
                </tbody>
            </table>
        </xsl:if>

        <xsl:apply-templates select="table"/>
        <xsl:apply-templates select="group"/>
    </div>
</xsl:template>

<xsl:template match="extension">
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
</xsl:template>

<xsl:template match="deployment">
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
</xsl:template>

<xsl:template match="exception">
    <div class="whitespaces">
        <div class="exception_stacktrace dropt" onclick="overflow(this);" style="cursor:default;" onmouseover="showPopup(this);" onmouseout="hidePopup();">
            <xsl:value-of select="."/>
            <span style="width:500px;">Click in order to make stacktrace scrollable!</span>
        </div>
    </div>
</xsl:template>

<xsl:template match="video">
    <div class="video">
        <xsl:choose>
            <xsl:when test="@phase = 'IN_TEST'">
                <h6><xsl:choose><xsl:when test="@message"><xsl:value-of select="@message"></xsl:value-of></xsl:when><xsl:otherwise>In-test video</xsl:otherwise></xsl:choose></h6>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
        </xsl:choose>
        <video controls=""><source src="{@link}" type="video/{@type}"/>Your browser does not support video tag.</video>
    </div>
</xsl:template>

<xsl:template name="all" match="/">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Arquillian test run report</title>
    <style type="text/css">
      body { margin: 1em; padding: 0; }
      h1 { font-size: 300%; margin: 0; padding: 0; display: inline-block; }
      h1 > img { float:left; margin: 0 5px 0 0; }
      h2 { font-size: 275%; margin: 0; padding: 0; }
      h3 { font-size: 250%; margin: 0; padding: 0; }
      h4 { font-size: 200%; margin: 0; padding: 0; }
      h5 { font-size: 150%; margin: 0; padding: 0; display: inline-block; }
      h5 > img { float:left; margin: 0 5px 0 0; }
      h6 { font-size: 125%; margin: 0; padding: 0; }
      .clear { clear: both; }
      .extensions { margin: 0 0 0 2em; }
      .extension { margin: 0 0 0 2em; }
      .suite,.configuration { margin: 0 0 0 2em; }
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
      .screenshotLeft:nth-child(1n) { margin: 0 0 0 2em; }
      .passed { color: #336633; }
      .failed { color: #FF0000; }
      .skipped { color: #FF9900; }
      .whitespaces { margin: 0 0 0 2em; white-space: pre; }
      .exception_stacktrace { overflow: hidden; height: 16%; }
      .exception_stacktrace::first-line { font-weight:bold; font-size: 110%; }
      #hoverpopup { visibility:hidden; position:absolute; top:0; left:0; color: white; background-color: black;}
      .testClass.collapsed>.testMethod,.containers.collapsed>.container,.testMethod.collapsed>div,.configuration.collapsed>div { display: none; }
      .collapsable:hover { cursor: pointer; }
      .collapsable::before { content: "[-] "; }
      .collapsable:hover::before { color: red; }
      .collapsed .collapsable::before { content: "[+] "; }
    </style>
    <script type="text/javascript"><![CDATA[
            function overflow(x) {
                if(x.style.overflow === "auto") {
                    x.style.overflow = "hidden";
                } else {
                    x.style.overflow = "auto";
                }
            }
            function showPopup(hoveritem)
            {
                var hp = document.getElementById("hoverpopup");
                var bottomLeft = getElementBottomLeft(hoveritem);
                hp.style.top = bottomLeft.bottom;
                hp.style.left = bottomLeft.left;
                hp.style.visibility = "visible";
            }
            function hidePopup()
            {
                var hp = document.getElementById("hoverpopup");
                hp.style.visibility = "hidden";
            }
            function getElementBottomLeft(ele) {
                var bottom = 0;
                var left = 0;
                while(ele.tagName != "BODY") {
                    bottom += ele.offsetTop + ele.offsetHeight;
                    left += ele.offsetLeft;
                    ele = ele.offsetParent;
                }
                return { bottom: bottom, left: left };
            }
            function collapse(sectionHeader) {
              sectionHeader.parentNode.classList.toggle('collapsed');
            }
            ]]>
    </script>
  </head>
  <body>
    <div id="main">
      <h1>
          <img alt="" src="data:image/gif;base64,R0lGODlhQABAAPf/ANTbc9PblLO+fPr78O7x0szS1ERcZk5kXFVqc/7+/SxHTjRBRUNYVvH17dTcmPj68LC6u/b45YiZdfj66/T34EBZYvH10UJaZNzjq11zZ+vx5W55V/j69tbiyjxNUzhSWuTr23qLktTZ21VsXZOictzm0ejv4brDxz5WYPv8+jxVXktibWp9hPf674OTmsfNyunuqXiKc73FyY+dpC5JUP3++vz9+/H02DROVvf66nOEi9zh45qoeuruyjJMVOXqwO3yzOzxt8PMgZqkovP23J2rdlRqbf39/ebp6s7XiuTpvPP23XyObjBKUmF1fdTZ0TZQWKq3fOHmtW2BbqSrqtDW2FlsbFNqZOLoubnEf/r89peenPr7+DtUXdfeoFlvZJyorePovtjd3vf59PP275WjpuDp1vb481xxeO7z6drhpa+7e6Kus/r78/T19vP09Wt/ZmR4a9/lsPn78vb46/T38PT44fL13ZilqoiSXJSiqJ+oZKSwtfT34/H13ens7UFOS7/HbnKFb8fO0tHZkEZeXk5gV664vYmZb6Sxd3OGZmV5f6WzeJKheIiXm8zVhElga0phbP///0hgavf56EhfakZeaPn77vj67Pz990lha/z99vX440dfafr88vX44vv88/n77/b55vv89DdFSfv89fb55zlHTDpJT/X45Pn67ff56Udeaf7+/v39+f///v39+DpTW/P33vz9+Pr88f7//kVdZ/7++/f55/3++fj67f3+/ff68Pf66fn67/n77drf4F5oUPz99TVPV/X48vj67ml9cKm0uPf67DFLU+LnuOLot/n67vr88/z9+fr89Pr89fD060piat/md2h8b0BZWz5XWsLJqcXMuYCKiePpjU5lbHWIb7K8v9DewUlUSujtxvHy8L3DwGx/h+Dk3E5kadrgkW16ffb44UpiaExjYPj67zdRUmp3eURbY7fAwzxHRFNhZbi/gN/k26GueqiytrS8avb46PP14YqZn2BucYyWlfX48fX56EZeZf///yH5BAEAAP8ALAAAAABAAEAAAAj/AP8JHEiwoEGCIvi4CBFphcMVCEK46CbioMWLGDMKHOQCgSZNkUKKfPgQwYxBGlOqFOiGj5NJkGJC+ghSZEOSDp3IWMnz4AkEnSpVmkRU5syaNnGucIKyJ88/Olh1mip0aFGj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" />
          <xsl:value-of select="/report/reportConfiguration/title"/>
      </h1>
    </div>

    <div class="configuration collapsed">
      <h4 onclick="collapse(this);" class="collapsable">Configuration</h4>
      <div class="properties">
        <table>
          <tbody>
            <xsl:for-each select="/report/property">
              <tr><td><xsl:value-of select="key"/></td><td><xsl:value-of select="value"/></td></tr>
            </xsl:for-each>
            <xsl:for-each select="/report/file">
              <tr><td colspan="2"><xsl:value-of select="@path"/></td></tr>
            </xsl:for-each>
          </tbody>
        </table>

        <xsl:apply-templates select="/report/table"/>
        <xsl:apply-templates select="/report/group"/>
      </div>

      <div class="extensions">
        <xsl:apply-templates select="/report/extension"/>
      </div>
    </div>

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
          <xsl:apply-templates select="table"/>
          <xsl:apply-templates select="group"/>
        </div>

        <xsl:apply-templates select="video"/>

        <div class="containers collapsed">
          <h3 onclick="collapse(this);" class="collapsable">Containers</h3>
          <xsl:for-each select="container">
          <div class="container" >
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
              <xsl:apply-templates select="deployment"/>
            </div>
          </div>
          </xsl:for-each>
        </div>

        <div class="testClasses">
          <h3>Tests</h3>
          <xsl:for-each select="class">
          <div class="testClass">
            <h4 onclick="collapse(this);" class="collapsable"><xsl:value-of select="@name"/> - <xsl:value-of select='@duration div 1000'/>s <xsl:if test="@runAsClient = 'true'"> (runs as client)</xsl:if></h4>
            <div class="properties">
              <table>
                <tbody>
                    <xsl:if test="@reportMessage">
                      <tr><td colspan="2"><xsl:value-of select="@reportMessage"/></td></tr>
                    </xsl:if>
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
              <xsl:apply-templates select="table"/>
              <xsl:apply-templates select="group"/>
            </div>

            <xsl:apply-templates select="video"/>

            <xsl:for-each select="method">
              <div class="testMethod collapsed">
              <xsl:choose>
                <xsl:when test="@result = 'PASSED'">
                  <h5 class="passed collapsable" onclick="collapse(this);">
                    <img alt="" src="data:image/png;base64,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" />
                    <xsl:value-of select="@name"/> (<xsl:value-of select='@duration div 1000'/>s)
                  </h5>
                </xsl:when>
                <xsl:when test="@result = 'FAILED'">
                  <h5 class="failed collapsable" onclick="collapse(this);">
                    <img alt="" src="data:image/png;base64,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" />
                    <xsl:value-of select="@name"/> (<xsl:value-of select='@duration div 1000'/>s)
                  </h5>
                </xsl:when>
              </xsl:choose>
                <div class="properties">
                  <table>
                    <tbody>
                      <xsl:if test="@reportMessage"><tr><td colspan="2"><xsl:value-of select="@reportMessage"/></td></tr></xsl:if>
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
                  <xsl:apply-templates select="video"/>
                  <xsl:apply-templates select="table"/>
                  <xsl:apply-templates select="group"/>
                </div>

                <xsl:apply-templates select="exception"/>

                <xsl:if test="screenshot">
                <div class="screenshotParent">
                  <xsl:for-each select="screenshot">
                    <xsl:choose>
                      <xsl:when test="(@width * /report/reportConfiguration/imageWidth div 100) &gt; /report/reportConfiguration/maxImageWidth">
                        <xsl:if test="@phase != 'IN_TEST'">
                          <p><a href="{@link}"><xsl:value-of select="@link"/></a></p>
                        </xsl:if>
                      </xsl:when>
                      <xsl:otherwise>
                        <div class="screenshotLeft">
                          <xsl:choose>
                            <xsl:when test="@phase = 'BEFORE'">
                              <h6>Before</h6>
                              <a href="{@link}">
                                <img src="{@link}">
                                  <xsl:attribute name="width"><xsl:value-of select="@width * /report/reportConfiguration/imageWidth div 100"/></xsl:attribute>
                                  <xsl:attribute name="height"><xsl:value-of select="@height * /report/reportConfiguration/imageHeight div 100"/></xsl:attribute>
                                </img>
                              </a>
                            </xsl:when>
                            <xsl:when test="@phase = 'AFTER'">
                              <h6>After</h6>
                              <a href="{@link}">
                                <img src="{@link}">
                                  <xsl:attribute name="width"><xsl:value-of select="@width * /report/reportConfiguration/imageWidth div 100"/></xsl:attribute>
                                  <xsl:attribute name="height"><xsl:value-of select="@height * /report/reportConfiguration/imageHeight div 100"/></xsl:attribute>
                                </img>
                              </a>
                            </xsl:when>
                            <xsl:when test="@phase = 'FAILED'">
                              <h6>Failed</h6>
                              <a href="{@link}">
                                <img src="{@link}">
                                  <xsl:attribute name="width"><xsl:value-of select="@width * /report/reportConfiguration/imageWidth div 100"/></xsl:attribute>
                                  <xsl:attribute name="height"><xsl:value-of select="@height * /report/reportConfiguration/imageHeight div 100"/></xsl:attribute>
                                </img>
                              </a>
                            </xsl:when>
                            <xsl:when test="@phase = 'ON_EVERY_ACTION'">
                              <h6>On every action</h6>
                              <a href="{@link}">
                                <img src="{@link}">
                                  <xsl:attribute name="width"><xsl:value-of select="@width * /report/reportConfiguration/imageWidth div 100"/></xsl:attribute>
                                  <xsl:attribute name="height"><xsl:value-of select="@height * /report/reportConfiguration/imageHeight div 100"/></xsl:attribute>
                                </img>
                              </a>
                            </xsl:when>
                          </xsl:choose>
                        </div>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:for-each>
                  <div class="clear"/>
                </div>
                </xsl:if>
                <xsl:if test="screenshot">
                  <xsl:for-each select="screenshot">
                    <xsl:if test="@phase = 'IN_TEST'">
                      <div class="screenshotParent">
                        <div class="screenshotLeft">
                          <h6><xsl:choose><xsl:when test="@message"><xsl:value-of select="@message"></xsl:value-of></xsl:when><xsl:otherwise>In-test screenshot</xsl:otherwise></xsl:choose></h6>
                          <xsl:choose>
                            <xsl:when test="@width &gt; /report/reportConfiguration/maxImageWidth">
                              <p><a href="{@link}"><xsl:value-of select="@link"/></a></p>
                            </xsl:when>
                            <xsl:otherwise>
                              <a href="{@link}">
                                <img src="{@link}">
                                  <xsl:attribute name="width"><xsl:value-of select="@width * /report/reportConfiguration/imageWidth div 100"/></xsl:attribute>
                                  <xsl:attribute name="height"><xsl:value-of select="@height * /report/reportConfiguration/imageHeight div 100"/></xsl:attribute>
                                </img>
                              </a>
                            </xsl:otherwise>
                          </xsl:choose>
                        </div>
                      </div>
                      <div class="clear"/>
                    </xsl:if>
                  </xsl:for-each>
                </xsl:if>
              </div>
            </xsl:for-each>
          </div>
          </xsl:for-each>
        </div>
      </div>
    </xsl:for-each>
        <font size="2">Arquillian name and logo are registered trademarks of Red Hat Inc.</font>
        <div id="hoverpopup">
            Click in order to make stacktrace un/scrollable!
        </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>

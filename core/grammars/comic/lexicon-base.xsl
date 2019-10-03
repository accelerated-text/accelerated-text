<?xml version="1.0"?>
<!-- 
Copyright (C) 2003-4 University of Edinburgh (Michael White)
$Revision: 1.32 $, $Date: 2005/07/20 15:59:08 $ 

See ../core-en/lexicon.xsl for comments re grammar.

The semantic roles are taken from FrameNet where possible.

NB: These namespace declarations seem to work with the version of Xalan 
    that comes with JDK 1.4.  In Xalan 2.5, the redirect namespace is 
    supposed to be declared as http://xml.apache.org/xalan/redirect, 
    but giving the classname (magically) seems to work.  
    With newer versions of Xalan, different namespace declarations may be required. 
-->
<xsl:transform 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xalan2="http://xml.apache.org/xslt"
  xmlns:redirect="org.apache.xalan.lib.Redirect" 
  extension-element-prefixes="redirect"
  exclude-result-prefixes="xalan xalan2">

  
  <!-- ***** Import Core Lexicon Definitions ***** -->
  <xsl:import href="../core-en/lexicon.xsl"/>
  
  <xsl:output indent="yes" xalan2:indent-amount="2"/> 
  <xsl:strip-space elements="*"/>

  
  <!-- ***** Start Output Here ***** -->
  <xsl:template match="/">
  <ccg-lexicon name="comic" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="../lexicon.xsd"
  >

  <!-- ***** Feature Declarations ******  -->
  <xsl:call-template name="add-feature-declarations"/>
  
  
  <!-- ***** Relation Sorting ******  -->
  <relation-sorting order=
    "BoundVar PairedWith
     Restr Body 
     Det Card Num 
     Arg Arg1 Arg2 Of
     Core Trib
     First Last List EqL
     Agent Experiencer Fig FigInv Owner
     Artifact Creator ElementOf Cognizer Communicator Perceiver 
     *
     Beneficiary Ground Poss Pred Prop Situation
     Chosen Content Material Phenomenon Referent Where
     Source
     Location 
     HasProp GenOwner
     GenRel Next"/>

    
  <!-- ***** Derived Categories and Families ***** -->
  <xsl:call-template name="add-core-families"/>
  
  
  <!-- By-Creator -->
  <xsl:variable name="X.Creator.Y">  
    <lf>
      <satop nomvar="X:sem-obj">
        <diamond mode="Creator"><nomvar name="Y:causal-agent"/></diamond>
      </satop>
    </lf>
  </xsl:variable>

  <xsl:variable name="P.has-rel.Of.X.Creator.Y">
    <xsl:call-template name="make-has-rel-lf">
      <xsl:with-param name="rel">Creator</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  
  <family name="By-Creator" pos="Prep" closed="true" indexRel="Creator">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($prep.n-postmod)/*"/>
        <xsl:with-param name="ext" select="$X.Creator.Y"/>
      </xsl:call-template>
    </entry>
    <entry name="Predicative">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($pred.prep)/*"/>
        <xsl:with-param name="ext" select="$P.has-rel.Of.X.Creator.Y"/>
      </xsl:call-template>
    </entry>
  </family>
  
  <!-- From-Source -->
  <xsl:variable name="X.Source.Y">
    <lf>
      <satop nomvar="X:sem-obj">
        <diamond mode="Source"><nomvar name="Y:abstraction"/></diamond>
      </satop>
    </lf>
  </xsl:variable>

  <xsl:variable name="P.has-rel.Of.X.Source.Y">
    <xsl:call-template name="make-has-rel-lf">
      <xsl:with-param name="rel">Source</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  
  <family name="Prep-Source" pos="Prep" closed="true" indexRel="Source">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($prep.n-postmod)/*"/>
        <xsl:with-param name="ext" select="$X.Source.Y"/>
      </xsl:call-template>
    </entry>
    <entry name="Predicative">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($pred.prep)/*"/>
        <xsl:with-param name="ext" select="$P.has-rel.Of.X.Source.Y"/>
      </xsl:call-template>
    </entry>
  </family>
  
  
  <!-- Based-On: uses Artifact, Source roles -->
  <xsl:variable name="pred.adj.np">
    <xsl:call-template name="extend">
      <xsl:with-param name="elt" select="xalan:nodeset($pred.adj)/*"/>
      <xsl:with-param name="ext">
        <slash dir="/" mode="&lt;"/>
        <xsl:copy-of select="$np.3.Y.acc"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
  
  <xsl:variable name="P.Default.Artifact.X.Source.Y">  
    <lf>
      <satop nomvar="P:proposition">
        <prop name="[*DEFAULT*]"/>
        <diamond mode="Artifact"><nomvar name="X:sem-obj"/></diamond>
        <diamond mode="Source"><nomvar name="Y:abstraction"/></diamond>
      </satop>
    </lf>
  </xsl:variable>

  <family name="Based-On" pos="Adj" closed="true">
    <entry name="Predicative">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($pred.adj.np)/*"/>
        <xsl:with-param name="ext" select="$P.Default.Artifact.X.Source.Y"/>
      </xsl:call-template>
    </entry>
  </family>

  
  <!-- Canned-VP -->
  <xsl:variable name="E.Default.Arg.X">
    <lf>
      <satop nomvar="E:situation">
        <prop name="[*DEFAULT*]"/>
        <diamond mode="Arg"><nomvar name="X:sem-obj"/></diamond>
      </satop>
    </lf>
  </xsl:variable>
  
  <family name="Canned-VP" pos="V" closed="true">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($iv)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Arg.X"/>
      </xsl:call-template>
    </entry>
  </family>
  
  
  <!-- Drawing-From: uses Artifact, Source roles -->
  <xsl:variable name="E.Default.Artifact.X.Source.Y">  
    <lf>
      <satop nomvar="E:proposition">
        <prop name="[*DEFAULT*]"/>
        <diamond mode="Artifact"><nomvar name="X:sem-obj"/></diamond>
        <diamond mode="Source"><nomvar name="Y:abstraction"/></diamond>
      </satop>
    </lf>
  </xsl:variable>

  <family name="Drawing-From" pos="V" closed="true">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($tv.from)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Artifact.X.Source.Y"/>
      </xsl:call-template>
    </entry>
  </family>
  
  <!-- Featuring: uses Artifact, Poss, Where roles -->
  <!-- 
    NB: 'Where' is used instead of the more general Location role, 
        in order to avoid problems with lex lookup in realization.
  -->
  <xsl:variable name="E.Default.Artifact.X.Poss.Y">  
    <lf>
      <satop nomvar="E:proposition">
        <prop name="[*DEFAULT*]"/>
        <diamond mode="Artifact"><nomvar name="X:sem-obj"/></diamond>
        <diamond mode="Poss"><nomvar name="Y:sem-obj"/></diamond>
      </satop>
    </lf>
  </xsl:variable>

  <xsl:variable name="E.Default.Artifact.X.Poss.Y.Where.P">  
    <xsl:call-template name="extend">
      <xsl:with-param name="elt" select="xalan:nodeset($E.Default.Artifact.X.Poss.Y)/*"/>
      <xsl:with-param name="ext">
        <satop nomvar="E:proposition">
          <diamond mode="Where"><nomvar name="P:proposition"/></diamond>
        </satop>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:variable>

  <family name="Featuring" pos="V" closed="true">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($tv)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Artifact.X.Poss.Y"/>
      </xsl:call-template>
    </entry>
    <entry name="TV-Plus-Pred-Y">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($tv.plus.pred.Y)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Artifact.X.Poss.Y.Where.P"/>
      </xsl:call-template>
    </entry>
  </family>
  
  <family name="Going-With" pos="V" closed="true">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($tv.with)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Cognizer.X.Chosen.Y"/>
      </xsl:call-template>
    </entry>
  </family>
  
  <!-- Looking-At/Seeing: uses Perceiver, Phenomenon roles -->
  <family name="Looking-At" pos="V" closed="true">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($tv.at)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Perceiver.X.Phenomenon.Y"/>
      </xsl:call-template>
    </entry>
  </family>
  
  <family name="Seeing" pos="V" closed="true">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($tv)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Perceiver.X.Phenomenon.Y"/>
      </xsl:call-template>
    </entry>
    <entry name="Intransitive">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($iv)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Perceiver.X"/>
      </xsl:call-template>
    </entry>
  </family>
  
  <!-- Meaning: uses Communicator, Referent roles -->
  <xsl:variable name="E.Default.Communicator.X.Referent.Y">  
    <lf>
      <satop nomvar="E:proposition">
        <prop name="[*DEFAULT*]"/>
        <diamond mode="Communicator"><nomvar name="X:causal-agent"/></diamond>
        <diamond mode="Referent"><nomvar name="Y:sem-obj"/></diamond>
      </satop>
    </lf>
  </xsl:variable>

  <family name="Meaning" pos="V" closed="true">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($tv)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Communicator.X.Referent.Y"/>
      </xsl:call-template>
    </entry>
  </family>
  
  <!-- Using: uses Artifact, Material roles -->
  <xsl:variable name="E.Default.Artifact.X.Material.Y">  
    <lf>
      <satop nomvar="E:proposition">
        <prop name="[*DEFAULT*]"/>
        <diamond mode="Artifact"><nomvar name="X:sem-obj"/></diamond>
        <diamond mode="Material"><nomvar name="Y:phys-obj"/></diamond>
      </satop>
    </lf>
  </xsl:variable>

  <family name="Using" pos="V" closed="true">
    <entry name="Primary">
      <xsl:call-template name="extend">
        <xsl:with-param name="elt" select="xalan:nodeset($tv)/*"/>
        <xsl:with-param name="ext" select="$E.Default.Artifact.X.Material.Y"/>
      </xsl:call-template>
    </entry>
  </family>
  
  <!-- Gesture -->
  <family name="Gesture" pos="Gest" closed="true" coartRel="gest">
    <entry name="Primary">
      <complexcat>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          </fs>
        </atomcat>
        <slash dir="|" mode="*"/>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="X:sem-obj">
            <diamond mode="gest"><prop name="+"/></diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
    <!-- 
    <entry name="NP">
      <complexcat>
        <atomcat type="np">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          </fs>
        </atomcat>
        <slash dir="|" mode="*"/>
        <atomcat type="np">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="X:sem-obj">
            <diamond mode="gest"><prop name="+"/></diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
    -->
  </family>
  
  <!-- Nod (for example) -->
  <family name="Nod" pos="Nod" closed="true" coartRel="nod">
    <entry name="Primary">
      <complexcat>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          </fs>
        </atomcat>
        <slash dir="|" mode="*"/>
        <atomcat type="n">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          </fs>
        </atomcat>
        <lf>
          <satop nomvar="X:sem-obj">
            <diamond mode="nod"><prop name="+"/></diamond>
          </satop>
        </lf>
      </complexcat>
    </entry>
  </family>
  
  </ccg-lexicon>

  
  <!-- ***** Write type changing and lexical factor rules to unary-rules.xml ***** -->
  <redirect:write file="unary-rules.xml">
  <unary-rules>
    <xsl:call-template name="add-unary-rules"/>

    <!-- Num Element-Of -->
    <typechanging name="num-elt">
      <arg>
        <atomcat type="num">
          <fs id="2">
            <feat attr="index"><lf><nomvar name="X"/></lf></feat>
          </fs>
        </atomcat>
      </arg>
      <result>
        <complexcat>
          <atomcat type="np">
            <fs inheritsFrom="2">
              <feat attr="index"><lf><nomvar name="X"/></lf></feat>
              <feat attr="info"><lf><var name="INFO"/></lf></feat>
              <feat attr="owner"><lf><var name="OWNER"/></lf></feat>
              <feat attr="pers" val="3rd"/>
            </fs>
          </atomcat>
          <slash dir="/" mode="^"/>
          <atomcat type="pp">
            <fs>
              <feat attr="index"><lf><nomvar name="Y"/></lf></feat>
              <feat attr="lex" val="of"/>
            </fs>
          </atomcat>
          <lf>
            <satop nomvar="X:sem-obj">
              <diamond mode="det"><prop name="nil"/></diamond>
              <diamond mode="ElementOf"><nomvar name="Y:sem-obj"/></diamond>
            </satop>
          </lf>
        </complexcat>
      </result>
    </typechanging>
  </unary-rules>
  </redirect:write>
  </xsl:template>

</xsl:transform>


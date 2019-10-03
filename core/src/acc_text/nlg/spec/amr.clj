(ns acc-text.nlg.spec.amr
  (:require [clojure.spec.alpha :as s]))

(s/def ::id string?)
(s/def ::type string?)
(s/def ::description string?)

(s/def ::example string?)
(s/def ::examples (s/coll-of ::example :min-count 1))

(s/def ::member string?)
(s/def ::members (s/coll-of ::member :min-count 1))

(s/def ::role (s/keys :req [::type]))
(s/def ::roles (s/coll-of ::role :min-count 1))

(s/def ::syntax string?)
(s/def ::semantics string?)

(s/def ::frame (s/keys :req [::description ::examples ::syntax ::semantics]))
(s/def ::frames (s/coll-of ::frame :min-count 1))

(s/def ::concept (s/keys :req [::id ::members ::roles ::frames]))

;; SELRESTRS - Selectional Restrictions
;; Each thematic role listed in a class may optionally be further characterized by certain selectional restrictions,
;; which provide more information about the nature of a given role.

;; <VNCLASS ID="appear-48.1.1">
;;     <MEMBERS>
;;         <MEMBER name="appear" wn="appear%2:30:00 appear%2:30:01" grouping="appear.02"/>
;;         <MEMBER name="arise" wn="arise%2:42:00 arise%2:42:01" grouping="arise.01"/>
;;         <MEMBER name="awake" wn="awake%2:29:00" grouping=""/>
;;         <MEMBER name="awaken" wn="awaken%2:29:01" grouping="awaken.01"/>
;;         <MEMBER name="break" wn="break%2:30:04 break%2:42:14 break%2:38:09 break%2:32:05" grouping="break.07"/>
;;         <MEMBER name="burst" wn="burst%2:30:02" grouping="burst.02"/>
;;     </MEMBERS>
;;     <THEMROLES>
;;         <THEMROLE type="Theme">
;;             <SELRESTRS />
;;         </THEMROLE>
;;         <THEMROLE type="Location">
;;             <SELRESTRS/>
;;         </THEMROLE>
;;     </THEMROLES>
;;     <FRAMES>
;;         <FRAME>
;;             <DESCRIPTION descriptionNumber="0.1" primary="NP V" secondary="Basic Intransitive" xtag=""/>
;;             <EXAMPLES>
;;                 <EXAMPLE>A ship appeared.</EXAMPLE>
;;             </EXAMPLES>
;;             <SYNTAX>
;;                 <NP value="Theme">
;;                     <SYNRESTRS/>
;;                 </NP>
;;                 <VERB/>
;;             </SYNTAX>
;;             <SEMANTICS>
;;                 <PRED value="appear">
;;                     <ARGS>
;;                         <ARG type="Event" value="during(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                     </ARGS>
;;                 </PRED>
;;             </SEMANTICS>
;;         </FRAME>
;;         <FRAME>
;;             <DESCRIPTION descriptionNumber="0.1" primary="NP V PP.location" secondary="PP; Location-PP" xtag=""/>
;;             <EXAMPLES>
;;                 <EXAMPLE>A ship appeared on the horizon.</EXAMPLE>
;;             </EXAMPLES>
;;             <SYNTAX>
;;                 <NP value="Theme">
;;                     <SYNRESTRS/>
;;                 </NP>
;;                 <VERB/>
;;                 <PREP>
;;                     <SELRESTRS>
;;                         <SELRESTR Value="+" type="loc"/>
;;                     </SELRESTRS>
;;                 </PREP>
;;                 <NP value="Location">
;;                     <SYNRESTRS/>
;;                 </NP>
;;             </SYNTAX>
;;             <SEMANTICS>
;;                 <PRED value="appear">
;;                     <ARGS>
;;                         <ARG type="Event" value="during(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                     </ARGS>
;;                 </PRED>
;;                 <PRED bool="!" value="Prep">
;;                     <ARGS>
;;                         <ARG type="Event" value="start(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                         <ARG type="ThemRole" value="Location"/>
;;                     </ARGS>
;;                 </PRED>
;;                 <PRED value="Prep">
;;                     <ARGS>
;;                         <ARG type="Event" value="end(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                         <ARG type="ThemRole" value="Location"/>
;;                     </ARGS>
;;                 </PRED>
;;             </SEMANTICS>
;;         </FRAME>
;;         <FRAME>
;;             <DESCRIPTION descriptionNumber="6.1" primary="There V NP PP" secondary="NP-PP; Expletive-there Subject" xtag=""/>
;;             <EXAMPLES>
;;                 <EXAMPLE>There appeared a ship on the horizon.</EXAMPLE>
;;             </EXAMPLES>
;;             <SYNTAX>
;;                 <LEX value="there"/>
;;                 <VERB/>
;;                 <NP value="Theme">
;;                     <SYNRESTRS/>
;;                 </NP>
;;                 <PREP>
;;                     <SELRESTRS>
;;                         <SELRESTR Value="+" type="loc"/>
;;                     </SELRESTRS>
;;                 </PREP>
;;                 <NP value="Location">
;;                     <SYNRESTRS/>
;;                 </NP>
;;             </SYNTAX>
;;             <SEMANTICS>
;;                 <PRED value="appear">
;;                     <ARGS>
;;                         <ARG type="Event" value="during(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                     </ARGS>
;;                 </PRED>
;;                 <PRED bool="!" value="Prep">
;;                     <ARGS>
;;                         <ARG type="Event" value="start(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                         <ARG type="ThemRole" value="Location"/>
;;                     </ARGS>
;;                 </PRED>
;;                 <PRED value="Prep">
;;                     <ARGS>
;;                         <ARG type="Event" value="end(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                         <ARG type="ThemRole" value="Location"/>
;;                     </ARGS>
;;                 </PRED>
;;             </SEMANTICS>
;;             <!--most verbs-->
;;         </FRAME>
;;         <FRAME>
;;             <DESCRIPTION descriptionNumber="6.2" primary="PP.location V NP" secondary="Locative Inversion" xtag=""/>
;;             <EXAMPLES>
;;                 <EXAMPLE>On the horizon appeared a large ship.</EXAMPLE>
;;             </EXAMPLES>
;;             <SYNTAX>
;;                 <PREP>
;;                     <SELRESTRS>
;;                         <SELRESTR Value="+" type="loc"/>
;;                     </SELRESTRS>
;;                 </PREP>
;;                 <NP value="Location">
;;                     <SYNRESTRS/>
;;                 </NP>
;;                 <VERB/>
;;                 <NP value="Theme">
;;                     <SYNRESTRS/>
;;                 </NP>
;;             </SYNTAX>
;;             <SEMANTICS>
;;                 <PRED value="appear">
;;                     <ARGS>
;;                         <ARG type="Event" value="during(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                     </ARGS>
;;                 </PRED>
;;                 <PRED bool="!" value="Prep">
;;                     <ARGS>
;;                         <ARG type="Event" value="start(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                         <ARG type="ThemRole" value="Location"/>
;;                     </ARGS>
;;                 </PRED>
;;                 <PRED value="Prep">
;;                     <ARGS>
;;                         <ARG type="Event" value="end(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                         <ARG type="ThemRole" value="Location"/>
;;                     </ARGS>
;;                 </PRED>
;;             </SEMANTICS>
;;             <!--most verbs-->
;;         </FRAME>
;;         <FRAME>
;;             <DESCRIPTION descriptionNumber="" primary="NP V ADV" secondary="ADVP-PRED; here/there" xtag=""/>
;;             <EXAMPLES>
;;                 <EXAMPLE>It appeared there.</EXAMPLE>
;;             </EXAMPLES>
;;             <SYNTAX>
;;                 <NP value="Theme">
;;                     <SYNRESTRS/>
;;                 </NP>
;;                 <VERB/>
;;                 <NP value="Location">
;;                     <SYNRESTRS>
;;                         <SYNRESTR Value="+" type="adv_loc"/>
;;                     </SYNRESTRS>
;;                 </NP>
;;             </SYNTAX>
;;             <SEMANTICS>
;;                 <PRED value="appear">
;;                     <ARGS>
;;                         <ARG type="Event" value="during(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                     </ARGS>
;;                 </PRED>
;;                 <PRED bool="!" value="location">
;;                     <ARGS>
;;                         <ARG type="Event" value="start(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                         <ARG type="ThemRole" value="Location"/>
;;                     </ARGS>
;;                 </PRED>
;;                 <PRED value="location">
;;                     <ARGS>
;;                         <ARG type="Event" value="end(E)"/>
;;                         <ARG type="ThemRole" value="Theme"/>
;;                         <ARG type="ThemRole" value="Location"/>
;;                     </ARGS>
;;                 </PRED>
;;             </SEMANTICS>
;;         </FRAME>
;;     </FRAMES>
;;     <SUBCLASSES/>
;; </VNCLASS>

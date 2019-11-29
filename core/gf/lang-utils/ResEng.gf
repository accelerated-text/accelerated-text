--# -path=.:../abstract:../common:../../prelude

--1 English auxiliary operations.

-- This module contains operations that are needed to make the
-- resource syntax work. To define everything that is needed to
-- implement $Test$, it moreover contains regular lexical
-- patterns needed for $Lex$.

resource ResEng = ParamX ** open Prelude in {

  flags optimize=all ;


-- Some parameters, such as $Number$, are inherited from $ParamX$.

--2 For $Noun$

-- This is case as needed when inflecting nouns.

  param
    Case = Nom | Gen ;

-- This is the worst-case $Case$ needed for pronouns: I, me, my, mine

    NPCase = NCase Case | NPAcc | NPNomPoss ;

-- Useful macros and conversions:

  oper
    npNom : NPCase = NCase Nom ;
    npGen : NPCase = NCase Gen ;

    npcase2case : NPCase -> Case = \nc -> case nc of {NCase c => c ; _ => Nom} ;

-- Agreement of $NP$ has 8 values. $Gender$ is needed for "who"/"which" and
-- for "himself"/"herself"/"itself".

  param
    Agr = AgP1 Number | AgP2 Number | AgP3Sg Gender | AgP3Pl Gender ;

  param 
    Gender = Neutr | Masc | Fem ;


--2 For $Verb$

-- Only these five forms are needed for open-lexicon verbs.

  param
    VForm = 
       VInf
     | VPres
     | VPPart
     | VPresPart
     | VPast      --# notpresent
     ;

-- Auxiliary verbs have special negative forms.

    VVForm = 
       VVF VForm
     | VVPresNeg
     | VVPastNeg  --# notpresent
     ;

-- The order of sentence is needed already in $VP$ because the need of "do" depends on it. 
-- $ODir True$ means contracted forms ("'s", "'d", "'ve". "'re").
-- Notice that inverted forms ($OQuest$) don't allow verb contractions: *"'s he arrived".

    Order = ODir Bool | OQuest ;

oper
    oDir = ODir False ;

-- The type of complement of a VV

param
    VVType = VVAux | VVInf | VVPresPart ; -- can do / try to do / start doing

--2 For $Adjective$

    AForm = AAdj Degree Case | AAdv ;

--2 For $Relative$
 
    RAgr = RNoAg | RAg Agr ;
    RCase = RPrep Gender | RC Gender NPCase ;

--2 For $Numeral$

    CardOrd = NCard | NOrd ;
    DForm = unit | teen | ten  ;

--2 Transformations between parameter types

  oper
    toAgr : Number -> Person -> Gender -> Agr = \n,p,g -> 
      case p of {
        P1 => AgP1 n ;
        P2 => AgP2 n ;
        P3 => case n of {
          Sg => AgP3Sg g ;
          Pl => AgP3Pl g
          }
        } ;

    fromAgr : Agr -> {n : Number ; p : Person ; g : Gender} = \a -> case a of {
      AgP1 n => {n = n ; p = P1 ; g = Masc} ;
      AgP2 n => {n = n ; p = P2 ; g = Masc} ;
      AgP3Pl g => {n = Pl ; p = P3 ; g = g} ;
      AgP3Sg g => {n = Sg ; p = P3 ; g = g}
      } ;

    agrP3 : Number -> Agr = \n -> agrgP3 n Neutr ;

    agrgP3 : Number -> Gender -> Agr = \n,g -> toAgr n P3 g ;

    conjAgr : Agr -> Agr -> Agr = \a0,b0 -> 
      let a = fromAgr a0 ; b = fromAgr b0 
      in
      toAgr
        (conjNumber a.n b.n)
        (conjPerson a.p b.p) a.g ;

-- For $Lex$.

-- For each lexical category, here are the worst-case constructors.

    mkNoun : (_,_,_,_ : Str) -> {s : Number => Case => Str} = 
      \man,mans,men,mens -> {
      s = table {
        Sg => table {
          Gen => mans ;
          _ => man
          } ;
        Pl => table {
          Gen => mens ;
          _ => men
          }
        }
      } ;

    mkAdjective : (_,_,_,_ : Str) -> {s : AForm => Str; isPre : Bool; lock_A : {}} = 
      \good,better,best,well -> lin A {
      s = table {
        AAdj Posit  c => (regGenitiveS good) ! c ;
        AAdj Compar c => (regGenitiveS better) ! c ;
        AAdj Superl c => (regGenitiveS best) ! c ;
        AAdv          => well
        } ;
      isPre = True
      } ;

    mkVerb : (_,_,_,_,_ : Str) -> Verb = 
      \go,goes,went,gone,going -> {
      s = table {
        VInf   => go ;
        VPres  => goes ;
        VPast  => went ; --# notpresent
        VPPart => gone ;
        VPresPart => going
        } ;
      p = [] ; -- no particle
      isRefl = False
      } ;

    mkIP : (i,me,my : Str) -> Number -> {s : NPCase => Str ; n : Number} =
     \i,me,my,n -> let who = mkNP i me my n P3 Neutr in {
        s = who.s ; 
        n = n
        } ;

    mkNP : (i,me,my : Str) -> Number -> Person -> Gender -> 
     {s : NPCase => Str ; a : Agr} = \i,me,my,n,p,g -> 
   { s = table {
       NCase Nom => i ;
       NPAcc => me ;
       NCase Gen | NPNomPoss => my -- works for normal genitives, "whose", etc.
       } ;
     a = toAgr n p g ;
   };

    regNP : Str -> Number -> {s : NPCase => Str ; a : Agr} = \that,n ->
      mkNP that that (that + "'s") n P3 Neutr ;

    regGenitiveS : Str -> Case => Str = \s -> 
      table { Gen => genitiveS s; _ => s } ;

    genitiveS : Str -> Str = \dog ->
      case last dog of {
          "s" => dog + "'" ;
          _   => dog + "'s"
          };

-- We have just a heuristic definition of the indefinite article.
-- There are lots of exceptions: consonantic "e" ("euphemism"), consonantic
-- "o" ("one-sided"), vocalic "u" ("umbrella").

    artIndef = pre {
      "eu" | "Eu" | "uni" | "up" => "a" ;
      "un" => "an" ; 
      "a" | "e" | "i" | "o" | "A" | "E" | "I" | "O" => "an" ;
      "SMS" | "sms" => "an" ; ---
      _ => "a"
      } ;

    artDef = "the" ;

-- For $Verb$.

  Verb : Type = {
    s : VForm => Str ;
    p : Str ; -- verb particle
    isRefl : Bool
    } ;

  param
  CPolarity = 
     CPos
   | CNeg Bool ;  -- contracted or not

  oper
  contrNeg : Bool -> Polarity -> CPolarity = \b,p -> case p of {
    Pos => CPos ;
    Neg => CNeg b
    } ;

  VerbForms : Type =
    Tense => Anteriority => CPolarity => Order => Agr => 
      {aux, adv, fin, inf : Str} ; -- would, not, sleeps, slept

  VP : Type = {
    s   : VerbForms ;
    p   : Str ;   -- verb particle
    prp : Str ;   -- present participle 
    ptp : Str ;   -- past participle
    inf : Str ;   -- the infinitive form ; VerbForms would be the logical place
    ad  : Agr => Str ; -- sentence adverb (can be Xself, hence Agr)
    s2  : Agr => Str ; -- complement
    ext : Str ;        -- extreposed field such as S, QS, VP
    isSimple : Bool    -- regulates the place of participle used as adjective
    } ;


  SlashVP = VP ** {c2 : Str ;
                   gapInMiddle : Bool;
                   missingAdv : Bool -- The sentence has been through VPSlashPrep, and the only missing thing is just an adverbial and shouldn't affect the agreement.
} ;

  predVc : (Verb ** {c2 : Str}) -> SlashVP = \verb -> 
    predV verb ** {c2 = verb.c2 ; gapInMiddle = True; missingAdv = False} ;

  cBind : Str -> Str = \s -> Predef.BIND ++ ("'" + s) ;

  predV : Verb -> VP = \verb -> {
    s = \\t,ant,b,ord,agr => 
      let
        inf  = verb.s ! VInf ;
        fin  = presVerb verb agr ;
        part = verb.s ! VPPart ;
      in
      case <t,ant,b,ord> of {
        <Pres,Simul,CPos,ODir _>      => vff            fin [] ;
        <Pres,Simul,CPos,OQuest>      => vf (does agr)   inf ;
        <Pres,Anter,CPos,ODir True>   => vf (haveContr agr)   part ; --# notpresent
        <Pres,Anter,CPos,_>           => vf (have      agr)   part ; --# notpresent
        <Pres,Anter,CNeg c,ODir True> => vfn c (haveContr agr) (haventContr agr) part ; --# notpresent
        <Pres,Anter,CNeg c,_>         => vfn c (have agr) (havent agr) part ; --# notpresent
        <Past,Simul,CPos,ODir _>      => vff (verb.s ! VPast) [] ; --# notpresent
        <Past,Simul,CPos,OQuest>      => vf "did"        inf ; --# notpresent
        <Past,Simul,CNeg c,_>         => vfn c "did" "didn't"     inf ; --# notpresent
        <Past,Anter,CPos,ODir True>   => vf (cBind "d")         part ; --# notpresent
        <Past,Anter,CPos,_>           => vf "had"        part ; --# notpresent
        <Past,Anter,CNeg c,ODir True> => vfn c (cBind "d") (cBind "d not")     part ; --# notpresent
        <Past,Anter,CNeg c,_>         => vfn c "had" "hadn't"     part ; --# notpresent
        <Fut, Simul,CPos,ODir True>   => vf (cBind "ll")       inf ; --# notpresent
        <Fut, Simul,CPos,_>           => vf "will"       inf ; --# notpresent
        <Fut, Simul,CNeg c,ODir True> => vfn c (cBind "ll") (cBind "ll not")      inf ; --# notpresent
        <Fut, Simul,CNeg c,_>         => vfn c "will" "won't"      inf ; --# notpresent
        <Fut, Anter,CPos,ODir True>   => vf (cBind "ll")       ("have" ++ part) ; --# notpresent
        <Fut, Anter,CPos,_>           => vf "will"       ("have" ++ part) ; --# notpresent
        <Fut, Anter,CNeg c,ODir True> => vfn c (cBind "ll") (cBind "ll not") ("have" ++ part) ; --# notpresent
        <Fut, Anter,CNeg c,_>         => vfn c "will" "won't" ("have" ++ part) ; --# notpresent
        <Cond,Simul,CPos,ODir True>   => vf (cBind "d")      inf ; --# notpresent
        <Cond,Simul,CPos,_>           => vf "would"      inf ; --# notpresent
        <Cond,Simul,CNeg c,ODir True> => vfn c (cBind "d") (cBind "d not")   inf ; --# notpresent
        <Cond,Simul,CNeg c,_>         => vfn c "would" "wouldn't"   inf ; --# notpresent
        <Cond,Anter,CPos,ODir True>   => vf (cBind "d")      ("have" ++ part) ; --# notpresent
        <Cond,Anter,CPos,_>           => vf "would"      ("have" ++ part) ; --# notpresent
        <Cond,Anter,CNeg c,ODir True> => vfn c (cBind "d") (cBind "d not") ("have" ++ part) ; --# notpresent
        <Cond,Anter,CNeg c,_>         => vfn c "would" "wouldn't" ("have" ++ part) ; --# notpresent
        <Pres,Simul,CNeg c,_>         => vfn c (does agr) (doesnt agr) inf
        } ;
    p    = verb.p ;
    prp  = verb.s ! VPresPart ;
    ptp  = verb.s ! VPPart ;
    inf  = verb.s ! VInf ;
    ad   = \\_ => [] ;
    ext  = [] ;
    isSimple = True ; ---- but really depends on whether p == []
    s2 = \\a => if_then_Str verb.isRefl (reflPron ! a) []
    } ;

  predAux : Aux -> VP = \verb -> {
    s = \\t,ant,cb,ord,agr => 
      let 
        b = case cb of {
          CPos => Pos ;
          _ => Neg
          } ;
        inf  = verb.inf ;
        fin  = verb.pres ! b ! agr ;
        finp = verb.pres ! Pos ! agr ;
        cfin  = verb.contr ! b ! agr ;
        cfinp = verb.contr ! Pos ! agr ;
        part = verb.ppart ;
      in
      case <t,ant,cb,ord> of {
        <Pres,Anter,CPos,ODir True>   => vf (haveContr agr)   part ; --# notpresent
        <Pres,Anter,CPos,_>           => vf (have      agr)   part ; --# notpresent
        <Pres,Anter,CNeg c,ODir True> => vfn c (haveContr agr) (haventContr agr) part ; --# notpresent
        <Pres,Anter,CNeg c,_>         => vfn c (have agr) (havent agr) part ; --# notpresent
 
        <Past,Anter,CPos,ODir True>   => vf (cBind "d")         part ; --# notpresent
        <Past,Anter,CPos,_>           => vf "had"        part ; --# notpresent
        <Past,Anter,CNeg c,ODir True> => vfn c (cBind "d") (cBind "d not")     part ; --# notpresent
        <Past,Anter,CNeg c,_>         => vfn c "had" "hadn't"     part ; --# notpresent
        <Fut, Simul,CPos,ODir True>   => vf (cBind "ll")       inf ; --# notpresent
        <Fut, Simul,CPos,_>           => vf "will"       inf ; --# notpresent
        <Fut, Simul,CNeg c,ODir True> => vfn c (cBind "ll") (cBind "ll not")      inf ; --# notpresent
        <Fut, Simul,CNeg c,_>         => vfn c "will" "won't"      inf ; --# notpresent
        <Fut, Anter,CPos,ODir True>   => vf (cBind "ll")       ("have" ++ part) ; --# notpresent
        <Fut, Anter,CPos,_>           => vf "will"       ("have" ++ part) ; --# notpresent
        <Fut, Anter,CNeg c,ODir True> => vfn c (cBind "ll") (cBind "ll not") ("have" ++ part) ; --# notpresent
        <Fut, Anter,CNeg c,_>         => vfn c "will" "won't"("have" ++ part) ; --# notpresent
        <Cond,Simul,CPos,ODir True>   => vf (cBind "d")      inf ; --# notpresent
        <Cond,Simul,CPos,_>           => vf "would"      inf ; --# notpresent
        <Cond,Simul,CNeg c,ODir True> => vfn c (cBind "d") (cBind "d not")   inf ; --# notpresent
        <Cond,Simul,CNeg c,_>         => vfn c "would" "wouldn't"   inf ; --# notpresent
        <Cond,Anter,CPos,ODir True>   => vf (cBind "d")      ("have" ++ part) ; --# notpresent
        <Cond,Anter,CPos,_>           => vf "would"      ("have" ++ part) ; --# notpresent
        <Cond,Anter,CNeg c,ODir True> => vfn c (cBind "d") (cBind "d not") ("have" ++ part) ; --# notpresent
        <Cond,Anter,CNeg c,_>         => vfn c "would" "wouldn't" ("have" ++ part) ; --# notpresent

        <Past,Simul,CPos,  _>    => vf (verb.past ! b ! agr) [] ; --# notpresent
        <Past,Simul,CNeg c,  _> => vfn c (verb.past!Pos!agr)(verb.past!Neg!agr) [] ; --# notpresent
        <Pres,Simul,CPos, ODir True>  => vf cfin          [] ;
        <Pres,Simul,CPos,  _>         => vf fin           [] ;
        <Pres,Simul,CNeg c,ODir True> => vfn c cfinp fin  [] ;
        <Pres,Simul,CNeg c,  _>       => vfn c finp fin   []
 
        } ;
    p = [] ;
    prp = verb.prpart ;
    ptp = verb.ppart ;
    inf = verb.inf ;
    ad = \\_ => [] ;
    ext = [] ;
    isSimple = True ;
    s2 = \\_ => []
    } ;
        
  vff : Str -> Str -> {aux, adv, fin, inf : Str} = \x,y -> 
    {aux = [] ; adv = [] ; fin = x ; inf = y} ;

  vf : Str -> Str -> {aux, adv, fin, inf : Str} = \x,y -> vfn True x x y ;

  vfn : Bool -> Str -> Str -> Str -> {aux, fin, adv, inf : Str} = 
    \contr,x,y,z -> 
    case contr of {
      True  => {aux = y ; adv = [] ; fin = [] ; inf = z} ;
      False => {aux = x ; adv = "not" ; fin = [] ; inf = z}
      } ;

{- IL 24/04/2018 To fix scope of reflexives:
  a) ComplSlash ( … ReflVP … ) X:    reflexive should agree with X
    LangEng> l PredVP (UsePron i_Pron) (ComplSlash (SlashV2V beg_V2V (ReflVP (SlashV2a like_V2))) (UsePron he_Pron))
    I beg him to like /himself/
  b) ReflVP ... ( … ComplSlash … X): reflexive should agree with subject
    LangEng> l PredVP (UsePron i_Pron) (SelfAdVVP (ComplSlash (SlashV2VNP beg_V2V (UsePron he_Pron) (SlashV2a like_V2)) (UsePron he_Pron)))
    I /myself/ beg him to like him -}
  objAgr : { a : Agr } -> VP -> VP = \obj,vp -> vp ** {
    s2 = \\a => vp.s2 ! obj.a } ;

  insertObj : (Agr => Str) -> VP -> VP = \obj,vp -> vp ** {
    s2 = \\a => vp.s2 ! a ++ obj ! a ;
    isSimple = False ;
    } ;

  insertObjPre : (Agr => Str) -> VP -> VP = \obj,vp -> vp ** {
    s2 = \\a => obj ! a ++ vp.s2 ! a ;
    isSimple = False ;
    } ;

  insertObjc : (Agr => Str) -> SlashVP -> SlashVP = \obj,vp -> 
    insertObj obj vp ** {c2 = vp.c2 ; gapInMiddle = vp.gapInMiddle ; missingAdv = vp.missingAdv } ;
  insertExtrac : Str -> SlashVP -> SlashVP = \obj,vp -> 
    insertExtra obj vp ** {c2 = vp.c2 ; gapInMiddle = vp.gapInMiddle ; missingAdv = vp.missingAdv } ;

--- AR 7/3/2013 move the particle after the object
  insertObjPartLast : (Agr => Str) -> VP -> VP = \obj,vp -> {
    s = vp.s ;
    p = [] ;  -- remove particle from here
    prp = vp.prp ;
    ptp = vp.ptp ;
    inf = vp.inf ;
    ad = vp.ad ;
    s2 = \\a => obj ! a ++ vp.s2 ! a ++ vp.p ; -- and put it here ; corresponds to insertObjPre
    isSimple = False ;
    ext = vp.ext    
    } ;

--- The adverb should be before the finite verb.

  insertAdV : Str -> VP -> VP = \ad -> insertAdVAgr (\\_ => ad) ;

  insertAdVAgr : (Agr => Str) -> VP -> VP = \ad,vp -> {
    s = vp.s ;
    p = vp.p ;
    prp = vp.prp ;
    ptp = vp.ptp ;
    inf = vp.inf ;
    ad  = \\a => vp.ad ! a ++ ad ! a ;
    s2 = \\a => vp.s2 ! a  ;
    isSimple = False ;
    ext = vp.ext
    } ;

  insertExtra : Str -> VP -> VP = \e,vp -> {
    s = vp.s ;
    p = vp.p ;
    prp = vp.prp ;
    ptp = vp.ptp ;
    inf = vp.inf ;
    ad  = vp.ad ;
    s2 =  vp.s2  ;
    isSimple = False ;
    ext = vp.ext ++ e  --- there should be at most one, one might think; but: I would say that it will be raining if I saw clouds
    } ;

-- 

  predVV : {s : VVForm => Str ; p : Str ; typ : VVType} -> VP = \verb ->
    let verbs = verb.s
    in
    case verb.typ of {
      VVAux => predAux {
        pres,contr = table {
          Pos => \\_ => verbs ! VVF VPres ;
          Neg => \\_ => verbs ! VVPresNeg
          } ;
        past = table {                       --# notpresent
          Pos => \\_ => verbs ! VVF VPast ;  --# notpresent
          Neg => \\_ => verbs ! VVPastNeg    --# notpresent
          } ;    --# notpresent
        p = verb.p ;
        inf = verbs ! VVF VInf ;
        ppart = verbs ! VVF VPPart ;
        prpart = verbs ! VVF VPresPart ;
        } ;
      _    => predV {s = \\vf => verbs ! VVF vf ; p = verb.p ; isRefl = False}
      } ;

  presVerb : {s : VForm => Str} -> Agr -> Str = \verb -> 
    agrVerb (verb.s ! VPres) (verb.s ! VInf) ;

  infVP : VVType -> VP -> Bool -> Anteriority -> CPolarity -> Agr -> Str = \typ,vp,ad_pos,ant,cb,a ->
    case cb of {CPos => ""; _ => "not"} ++
    case ant of {
      Simul => case typ of {
                 VVAux => vp.ad ! a ++ vp.inf ; 
                 VVInf => case ad_pos of {            ---- this is the "split infinitive"
                            True  => vp.ad ! a ++ "to" ++ vp.inf ;
                            False => "to" ++ vp.ad ! a ++ vp.inf
                          } ;
                 _ => vp.ad ! a ++ vp.prp
               }
      ; Anter => case typ of {                                    --# notpresent
                 VVAux => "have" ++ vp.ad ! a ++ vp.ptp ;                --# notpresent
                 VVInf => case ad_pos of {                                   --# notpresent
                             True  => vp.ad ! a ++ "to" ++ "have" ++ vp.ptp ;        --# notpresent
                             False => "to" ++ "have" ++ vp.ad ! a ++ vp.ptp          --# notpresent
                          } ;                                                --# notpresent
                 _     => "having" ++ vp.ad ! a ++ vp.ptp                 --# notpresent
               }                                                       --# notpresent
    } ++ vp.p ++
    vp.s2 ! a ++ vp.ext ;

  agrVerb : Str -> Str -> Agr -> Str = \has,have,agr -> 
    case agr of {
      AgP3Sg _ => has ;
      _        => have
      } ;

  have   = agrVerb "has"     "have" ;
  havent = agrVerb "hasn't"  "haven't" ;
  does   = agrVerb "does"    "do" ;
  doesnt = agrVerb "doesn't" "don't" ;

  haveContr   = agrVerb (cBind "s")     (cBind "ve") ;
  haventContr = agrVerb (cBind "s not")  (cBind "ve not") ;

  Aux = {
    pres  : Polarity => Agr => Str ; 
    contr : Polarity => Agr => Str ;  -- contracted forms 
    past  : Polarity => Agr => Str ;  --# notpresent
    inf,ppart,prpart : Str
    } ;

  auxBe : Aux = {
    pres = \\b,a => case <b,a> of {
      <Pos,AgP1 Sg> => "am" ; 
      <Neg,AgP1 Sg> => ["am not"] ; --- am not I
      _ => agrVerb (posneg b "is")  (posneg b "are") a
      } ;
    contr = \\b,a => case <b,a> of {
      <Pos,AgP1 Sg> => cBind "m" ; 
      <Neg,AgP1 Sg> => cBind "m not" ; --- am not I
      _ => agrVerb (posneg b (cBind "s"))  (posneg b (cBind "re")) a
      } ;
    past = \\b,a => case a of {          --# notpresent
      AgP1 Sg | AgP3Sg _ => posneg b "was" ; --# notpresent
      _                  => (posneg b "were")  --# notpresent
      } ; --# notpresent
    inf  = "be" ;
    ppart = "been" ;
    prpart = "being"
    } ;

  posneg : Polarity -> Str -> Str = \p,s -> case p of {
    Pos => s ;
    Neg => s + "n't"
    } ;

  conjThat : Str = "that" ;

  reflPron : Agr => Str = table {
    AgP1 Sg      => "myself" ;
    AgP2 Sg      => "yourself" ;
    AgP3Sg Masc  => "himself" ;
    AgP3Sg Fem   => "herself" ;
    AgP3Sg Neutr => "itself" ;
    AgP1 Pl      => "ourselves" ;
    AgP2 Pl      => "yourselves" ;
    AgP3Pl _     => "themselves"
    } ;
    
  possPron : Agr => Str = table {
    AgP1 Sg      => "my" ;
    AgP2 Sg      => "your" ;
    AgP3Sg Masc  => "his" ;
    AgP3Sg Fem   => "her" ;
    AgP3Sg Neutr => "its" ;
    AgP1 Pl      => "our" ;
    AgP2 Pl      => "your" ;
    AgP3Pl _     => "their"
    } ;

-- For $Sentence$.

  Clause : Type = {
    s : Tense => Anteriority => CPolarity => Order => Str
    } ;

  mkClause : Str -> Agr -> VP -> Clause =
    \subj,agr,vp -> {
      s = \\t,a,b,o => 
        let 
          verb  = vp.s ! t ! a ! b ! o ! agr ;
          compl = vp.s2 ! agr ++ vp.ext
        in
        case o of {
          ODir _ => subj ++ verb.aux ++ verb.adv ++ vp.ad ! agr ++ verb.fin ++ verb.inf ++ vp.p ++ compl ;
          OQuest => verb.aux ++ subj ++ verb.adv ++ vp.ad ! agr ++ verb.fin ++ verb.inf ++ vp.p ++ compl
          }
    } ;

-- for pos/neg variation other than negation word, e.g. "there is a car"/"there is no car"
   posNegClause : Clause -> Clause -> Clause = \pos,neg -> {
     s = \\t,a,b,o => case b of {
         CPos  => pos.s ! t ! a ! b ! o ;
         _     => neg.s ! t ! a ! CPos ! o
         }
     } ;
     

-- For $Numeral$.

  mkNum : Str -> Str -> Str -> Str -> {s : DForm => CardOrd => Case => Str} = 
    \two, twelve, twenty, second ->
    {s = table {
       unit => table {NCard => regGenitiveS two ; NOrd => regGenitiveS second} ; 
       teen => \\c => mkCard c twelve ; 
       ten  => \\c => mkCard c twenty
       }
    } ;

  regNum : Str -> {s : DForm => CardOrd => Case => Str} = 
    \six -> mkNum six (six + "teen") (six + "ty") (regOrd six) ;

  regCardOrd : Str -> {s : CardOrd => Case => Str} = \ten ->
    {s = table {NCard => regGenitiveS ten ; 
		NOrd => regGenitiveS (regOrd ten)} } ;

  mkCard : CardOrd -> Str -> Case => Str = \o,ten -> 
    (regCardOrd ten).s ! o ; 

  regOrd : Str -> Str = \ten -> 
    case last ten of {
      "y" => init ten + "ieth" ;
      _   => ten + "th"
      } ;

  mkQuestion : 
      {s : Str} -> Clause -> 
      {s : Tense => Anteriority => CPolarity => QForm => Str} = \wh,cl ->
      {
      s = \\t,a,p => 
            let 
              cls = cl.s ! t ! a ! p ;
              why = wh.s
            in table {
              QDir   => why ++ cls ! OQuest ;
              QIndir => why ++ cls ! oDir    ---- enable "why he's here"
              }
      } ;

  finalComma : Str = pre {"," | "." => []; "" => SOFT_BIND ++ ","; _ => []} ;
  frontComma : Str = SOFT_BIND ++ "," ;
}

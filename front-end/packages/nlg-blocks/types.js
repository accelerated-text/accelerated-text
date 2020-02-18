export const UNKNOWN =      undefined;
export const ANY =          null;
export const BOOLEAN =      'Boolean';
export const DEFINITION =   'Definition';
export const LIST =         'List';
export const STRING =       'Str';

export const A =            'A';
export const A2 =           'A2';
export const AP =           'AP';
export const AdA =          'AdA';
export const AdN =          'AdN';
export const AdNalmost =    'AdNalmost';
export const AdNat =        'AdNat';
export const AdV =          'AdV';
export const Adv =          'Adv';
export const Ant =          'Ant';
export const CAdv =         'CAdv';
export const CN =           'CN';
export const CNvery =       'CNvery';
export const Card =         'Card';
export const Cl =           'Cl';
export const ClSlash =      'ClSlash';
export const Comp =         'Comp';
export const Conj =         'Conj';
export const Det =          'Det';
export const Dig =          'Dig';
export const Digits =       'Digits';
export const Gender =       'Gender';
export const IAdv =         'IAdv';
export const IComp =        'IComp';
export const IDet =         'IDet';
export const IP =           'IP';
export const IQuant =       'IQuant';
export const Imp =          'Imp';
export const ImpForm =      'ImpForm';
export const Interj =       'Interj';
export const ListAP =       'ListAP';
export const ListAdv =      'ListAdv';
export const ListNP =       'ListNP';
export const ListRS =       'ListRS';
export const ListS =        'ListS';
export const N =            'N';
export const N2 =           'N2';
export const N3 =           'N3';
export const NP =           'NP';
export const Num =          'Num';
export const Number =       'Number';
export const Numeral =      'Numeral';
export const Ord =          'Ord';
export const PConj =        'PConj';
export const PN =           'PN';
export const Phr =          'Phr';
export const Pol =          'Pol';
export const Predet =       'Predet';
export const Prep =         'Prep';
export const Pron =         'Pron';
export const Punct =        'Punct';
export const QCl =          'QCl';
export const QS =           'QS';
export const Quant =        'Quant';
export const RCl =          'RCl';
export const RP =           'RP';
export const RS =           'RS';
export const S =            'S';
export const SC =           'SC';
export const SSlash =       'SSlash';
export const Sub100 =       'Sub100';
export const Sub1000 =      'Sub1000';
export const Subj =         'Subj';
export const Temp =         'Temp';
export const Tense =        'Tense';
export const Text =         'Text';
export const Type =         'Type';
export const Unit =         'Unit';
export const Utt =          'Utt';
export const V =            'V';
export const V2 =           'V2';
export const V2A =          'V2A';
export const V2Q =          'V2Q';
export const V2S =          'V2S';
export const V2V =          'V2V';
export const V3 =           'V3';
export const VA =           'VA';
export const VP =           'VP';
export const VPSlash =      'VPSlash';
export const VQ =           'VQ';
export const VS =           'VS';
export const VV =           'VV';
export const Voc =          'Voc';

export const ATOMIC_VALUE = [ BOOLEAN, STRING ];
export const TEXT =         [ LIST, STRING ];
export const AMR =          [
    A, A2, AP, AdA, AdN, AdNalmost, AdNat, AdV, Adv, Ant, CAdv,
    CN, CNvery, Card, Cl, ClSlash, Comp, Conj, Det, Dig, Digits,
    Gender, IAdv, IComp, IDet, IP, IQuant, Imp, ImpForm, Interj,
    ListAP, ListAdv, ListNP, ListRS, ListS, N, N2, N3, NP, Num,
    Number, Numeral, Ord, PConj, PN, Phr, Pol, Predet, Prep, Pron,
    Punct, QCl, QS, Quant, RCl, RP, RS, S, SC, SSlash, Sub100,
    Sub1000, Subj, Temp, Tense, Text, Type, Unit, Utt, V, V2,
    V2A, V2Q, V2S, V2V, V3, VA, VP, VPSlash, VQ, VS, VV, Voc,
];
export const AMR_OR_TEXT =   TEXT.concat( AMR );

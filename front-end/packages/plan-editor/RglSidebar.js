import { h }                from 'preact';

import A                    from '../rgl-concepts/A';
import A2                   from '../rgl-concepts/A2';
import AP                   from '../rgl-concepts/AP';
import AdA                  from '../rgl-concepts/AdA';
import AdN                  from '../rgl-concepts/AdN';
import AdNalmost            from '../rgl-concepts/AdNalmost';
import AdNat                from '../rgl-concepts/AdNat';
import AdV                  from '../rgl-concepts/AdV';
import Adv                  from '../rgl-concepts/Adv';
import Ant                  from '../rgl-concepts/Ant';
import CAdv                 from '../rgl-concepts/CAdv';
import CN                   from '../rgl-concepts/CN';
import CNvery               from '../rgl-concepts/CNvery';
import Card                 from '../rgl-concepts/Card';
import Cl                   from '../rgl-concepts/Cl';
import ClSlash              from '../rgl-concepts/ClSlash';
import Comp                 from '../rgl-concepts/Comp';
import Conj                 from '../rgl-concepts/Conj';
import Det                  from '../rgl-concepts/Det';
import Dig                  from '../rgl-concepts/Dig';
import Digits               from '../rgl-concepts/Digits';
import Gender               from '../rgl-concepts/Gender';
import IAdv                 from '../rgl-concepts/IAdv';
import IComp                from '../rgl-concepts/IComp';
import IDet                 from '../rgl-concepts/IDet';
import IP                   from '../rgl-concepts/IP';
import IQuant               from '../rgl-concepts/IQuant';
import Imp                  from '../rgl-concepts/Imp';
import ImpForm              from '../rgl-concepts/ImpForm';
import Interj               from '../rgl-concepts/Interj';
import ListAP               from '../rgl-concepts/ListAP';
import ListAdv              from '../rgl-concepts/ListAdv';
import ListNP               from '../rgl-concepts/ListNP';
import ListRS               from '../rgl-concepts/ListRS';
import ListS                from '../rgl-concepts/ListS';
import N                    from '../rgl-concepts/N';
import N2                   from '../rgl-concepts/N2';
import N3                   from '../rgl-concepts/N3';
import NP                   from '../rgl-concepts/NP';
import Num                  from '../rgl-concepts/Num';
import Number               from '../rgl-concepts/Number';
import Numeral              from '../rgl-concepts/Numeral';
import Ord                  from '../rgl-concepts/Ord';
import PConj                from '../rgl-concepts/PConj';
import PN                   from '../rgl-concepts/PN';
import Phr                  from '../rgl-concepts/Phr';
import Pol                  from '../rgl-concepts/Pol';
import Predet               from '../rgl-concepts/Predet';
import Prep                 from '../rgl-concepts/Prep';
import Pron                 from '../rgl-concepts/Pron';
import Punct                from '../rgl-concepts/Punct';
import QCl                  from '../rgl-concepts/QCl';
import QS                   from '../rgl-concepts/QS';
import Quant                from '../rgl-concepts/Quant';
import RCl                  from '../rgl-concepts/RCl';
import RP                   from '../rgl-concepts/RP';
import RS                   from '../rgl-concepts/RS';
import S                    from '../rgl-concepts/S';
import SC                   from '../rgl-concepts/SC';
import SSlash               from '../rgl-concepts/SSlash';
import Str                  from '../rgl-concepts/Str';
import Sub100               from '../rgl-concepts/Sub100';
import Sub1000              from '../rgl-concepts/Sub1000';
import Subj                 from '../rgl-concepts/Subj';
import Temp                 from '../rgl-concepts/Temp';
import Tense                from '../rgl-concepts/Tense';
import Text                 from '../rgl-concepts/Text';
import Type                 from '../rgl-concepts/Type';
import Unit                 from '../rgl-concepts/Unit';
import Utt                  from '../rgl-concepts/Utt';
import V                    from '../rgl-concepts/V';
import V2                   from '../rgl-concepts/V2';
import V2A                  from '../rgl-concepts/V2A';
import V2Q                  from '../rgl-concepts/V2Q';
import V2S                  from '../rgl-concepts/V2S';
import V2V                  from '../rgl-concepts/V2V';
import V3                   from '../rgl-concepts/V3';
import VA                   from '../rgl-concepts/VA';
import VP                   from '../rgl-concepts/VP';
import VPSlash              from '../rgl-concepts/VPSlash';
import VQ                   from '../rgl-concepts/VQ';
import VS                   from '../rgl-concepts/VS';
import VV                   from '../rgl-concepts/VV';
import Voc                  from '../rgl-concepts/Voc';
import composeContexts      from '../compose-contexts/';
import DataManager          from '../data-manager/DataManager';
import Dictionary           from '../dictionary/Dictionary';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import ReaderConfiguration  from '../reader/Configuration';
import Sidebar              from '../sidebar/Sidebar';
import SidebarItem          from '../sidebar/Item';
import VariantReview        from '../variant-review/VariantReview';


export default composeContexts({
    openedDataFile:         OpenedFileContext,
    openedPlan:             OpenedPlanContext,
})(({
    className,
    openedDataFile: { file },
    openedPlan: {
        plan,
        loading,
    },
}) =>
    <Sidebar className={ className }>
        <SidebarItem title="A">
            <A />
        </SidebarItem>
        <SidebarItem title="A2">
            <A2 />
        </SidebarItem>
        <SidebarItem title="AP">
            <AP />
        </SidebarItem>
        <SidebarItem title="AdA">
            <AdA />
        </SidebarItem>
        <SidebarItem title="AdN">
            <AdN />
        </SidebarItem>
        <SidebarItem title="AdNalmost">
            <AdNalmost />
        </SidebarItem>
        <SidebarItem title="AdNat">
            <AdNat />
        </SidebarItem>
        <SidebarItem title="AdV">
            <AdV />
        </SidebarItem>
        <SidebarItem title="Adv">
            <Adv />
        </SidebarItem>
        <SidebarItem title="Ant">
            <Ant />
        </SidebarItem>
        <SidebarItem title="CAdv">
            <CAdv />
        </SidebarItem>
        <SidebarItem title="CN">
            <CN />
        </SidebarItem>
        <SidebarItem title="CNvery">
            <CNvery />
        </SidebarItem>
        <SidebarItem title="Card">
            <Card />
        </SidebarItem>
        <SidebarItem title="Cl">
            <Cl />
        </SidebarItem>
        <SidebarItem title="ClSlash">
            <ClSlash />
        </SidebarItem>
        <SidebarItem title="Comp">
            <Comp />
        </SidebarItem>
        <SidebarItem title="Conj">
            <Conj />
        </SidebarItem>
        <SidebarItem title="Det">
            <Det />
        </SidebarItem>
        <SidebarItem title="Dig">
            <Dig />
        </SidebarItem>
        <SidebarItem title="Digits">
            <Digits />
        </SidebarItem>
        <SidebarItem title="Gender">
            <Gender />
        </SidebarItem>
        <SidebarItem title="IAdv">
            <IAdv />
        </SidebarItem>
        <SidebarItem title="IComp">
            <IComp />
        </SidebarItem>
        <SidebarItem title="IDet">
            <IDet />
        </SidebarItem>
        <SidebarItem title="IP">
            <IP />
        </SidebarItem>
        <SidebarItem title="IQuant">
            <IQuant />
        </SidebarItem>
        <SidebarItem title="Imp">
            <Imp />
        </SidebarItem>
        <SidebarItem title="ImpForm">
            <ImpForm />
        </SidebarItem>
        <SidebarItem title="Interj">
            <Interj />
        </SidebarItem>
        <SidebarItem title="ListAP">
            <ListAP />
        </SidebarItem>
        <SidebarItem title="ListAdv">
            <ListAdv />
        </SidebarItem>
        <SidebarItem title="ListNP">
            <ListNP />
        </SidebarItem>
        <SidebarItem title="ListRS">
            <ListRS />
        </SidebarItem>
        <SidebarItem title="ListS">
            <ListS />
        </SidebarItem>
        <SidebarItem title="N">
            <N />
        </SidebarItem>
        <SidebarItem title="N2">
            <N2 />
        </SidebarItem>
        <SidebarItem title="N3">
            <N3 />
        </SidebarItem>
        <SidebarItem title="NP">
            <NP />
        </SidebarItem>
        <SidebarItem title="Num">
            <Num />
        </SidebarItem>
        <SidebarItem title="Number">
            <Number />
        </SidebarItem>
        <SidebarItem title="Numeral">
            <Numeral />
        </SidebarItem>
        <SidebarItem title="Ord">
            <Ord />
        </SidebarItem>
        <SidebarItem title="PConj">
            <PConj />
        </SidebarItem>
        <SidebarItem title="PN">
            <PN />
        </SidebarItem>
        <SidebarItem title="Phr">
            <Phr />
        </SidebarItem>
        <SidebarItem title="Pol">
            <Pol />
        </SidebarItem>
        <SidebarItem title="Predet">
            <Predet />
        </SidebarItem>
        <SidebarItem title="Prep">
            <Prep />
        </SidebarItem>
        <SidebarItem title="Pron">
            <Pron />
        </SidebarItem>
        <SidebarItem title="Punct">
            <Punct />
        </SidebarItem>
        <SidebarItem title="QCl">
            <QCl />
        </SidebarItem>
        <SidebarItem title="QS">
            <QS />
        </SidebarItem>
        <SidebarItem title="Quant">
            <Quant />
        </SidebarItem>
        <SidebarItem title="RCl">
            <RCl />
        </SidebarItem>
        <SidebarItem title="RP">
            <RP />
        </SidebarItem>
        <SidebarItem title="RS">
            <RS />
        </SidebarItem>
        <SidebarItem title="S">
            <S />
        </SidebarItem>
        <SidebarItem title="SC">
            <SC />
        </SidebarItem>
        <SidebarItem title="SSlash">
            <SSlash />
        </SidebarItem>
        <SidebarItem title="Str">
            <Str />
        </SidebarItem>
        <SidebarItem title="Sub100">
            <Sub100 />
        </SidebarItem>
        <SidebarItem title="Sub1000">
            <Sub1000 />
        </SidebarItem>
        <SidebarItem title="Subj">
            <Subj />
        </SidebarItem>
        <SidebarItem title="Temp">
            <Temp />
        </SidebarItem>
        <SidebarItem title="Tense">
            <Tense />
        </SidebarItem>
        <SidebarItem title="Text">
            <Text />
        </SidebarItem>
        <SidebarItem title="Type">
            <Type />
        </SidebarItem>
        <SidebarItem title="Unit">
            <Unit />
        </SidebarItem>
        <SidebarItem title="Utt">
            <Utt />
        </SidebarItem>
        <SidebarItem title="V">
            <V />
        </SidebarItem>
        <SidebarItem title="V2">
            <V2 />
        </SidebarItem>
        <SidebarItem title="V2A">
            <V2A />
        </SidebarItem>
        <SidebarItem title="V2Q">
            <V2Q />
        </SidebarItem>
        <SidebarItem title="V2S">
            <V2S />
        </SidebarItem>
        <SidebarItem title="V2V">
            <V2V />
        </SidebarItem>
        <SidebarItem title="V3">
            <V3 />
        </SidebarItem>
        <SidebarItem title="VA">
            <VA />
        </SidebarItem>
        <SidebarItem title="VP">
            <VP />
        </SidebarItem>
        <SidebarItem title="VPSlash">
            <VPSlash />
        </SidebarItem>
        <SidebarItem title="VQ">
            <VQ />
        </SidebarItem>
        <SidebarItem title="VS">
            <VS />
        </SidebarItem>
        <SidebarItem title="VV">
            <VV />
        </SidebarItem>
        <SidebarItem title="Voc">
            <Voc />
        </SidebarItem>
    </Sidebar>
);

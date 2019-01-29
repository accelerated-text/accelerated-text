import { h }            from 'preact';

import ItemControls     from './ItemControls';
import List             from './List';
import S                from './PlanSelector.sass';


export default () =>
    <div className={ S.className }>
        <List />
        <ItemControls />
    </div>;

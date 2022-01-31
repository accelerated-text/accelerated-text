/* eslint-disable react/no-danger */

import { h }                from 'preact';
import { marked }           from 'marked';

import gettingStarted       from './getting-started.md';
import keyMap               from './key-map.md';
import S                    from './Help.sass';


const START_HTML =          marked( gettingStarted );
const KEYMAP_HTML =         marked( keyMap );


export default ({ onClose }) =>
    <div className={ S.className }>
        <div
            className={ S.shortcuts }
            dangerouslySetInnerHTML={{ __html: KEYMAP_HTML }}
        />
        <div
            className={ S.gettingStarted }
            dangerouslySetInnerHTML={{ __html: START_HTML }}
        />
    </div>;

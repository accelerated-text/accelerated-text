import { h }                from 'preact';

import AutofocusDiv         from './AutofocusDiv';


export default ChildComponent => props =>
    <AutofocusDiv>
        <ChildComponent { ...props } />
    </AutofocusDiv>;

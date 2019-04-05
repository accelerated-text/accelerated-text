import { h }            from 'preact';

import AnElement        from './AnElement';


export default ({ element }) =>
    <p>{ element.children.map(
        child => <AnElement key={ child.id } element={ child } />
    )}</p>;

import { h }            from 'preact';


export default ({ element }) =>
    <div>
        { element.id }
        { element.type }
        { element.children && element.children.length } children
    </div>;

import { h }            from 'preact';


export default ({ className, justIcon, message }) =>
    <a
        className={ className }
        title={ message && message.toString() }
    >
        ℹ️
        { justIcon ? null : [ ' ', message ] }
    </a>;

import { h }            from 'preact';


const MESSAGE =         'The app is in an unexpected state. Please save your work and refresh the page.';

export default ({ className, justIcon, message = MESSAGE }) =>
    <a
        className={ className }
        title={ message && message.toString() }
    >
        ❓
        { justIcon ? null : [ ' ', message ] }
    </a>;

import { Component, VirtualComponent, h } from '/component.js';

const SayHello = ({
  test
}) => {
  if (test) {
    return h("coucou-coucou", {
      "country-code": "en"
    });
  } else {
    return null;
  }
};

class Test extends VirtualComponent {
  static get observedAttributes() {
    return ['country-code', 'test'];
  }

  render() {
    return h("div", null, "Hello", this.countryCode, h(SayHello, {
      test: this.test
    }), this.test ? h("coucou-coucou", {
      "country-code": "en"
    }) : null);
  }

}

export default Test;
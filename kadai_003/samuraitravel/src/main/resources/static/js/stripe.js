
const stripe = Stripe('pk_test_51QLGkCKfB0cyZLWcE8PpB89eTFdfMEIkTxiN0h1VqRUrMARjjMx8FexOgYOFW6MPiweiqeelKnSTDgAPa9kwcPcE00kMN8IHB1');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
  stripe.redirectToCheckout({
    sessionId: sessionId
  })
});